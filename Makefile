# ========================================================
# QA — variáveis
# ========================================================

# DEVICE é a posição do aparelho em `adb devices` (padrão: 1), ex.: make wifi DEVICE=2
DEVICE ?= 1
QA_PACKAGE ?= com.fstengineering.leasilycrm.dev
QA_SERIAL = $(shell adb devices | awk 'NR > 1 && $$2 == "device" {print $$1}' | sed -n '$(DEVICE)p')
QA_ADB = adb -s $(QA_SERIAL)
APP_PACKAGES := com.fstengineering.leasilycrm.dev com.fstengineering.leasilycrm
REQUIRE_DEVICE = test -n "$(QA_SERIAL)" || { echo "nenhum aparelho na posição $(DEVICE) de 'adb devices'"; exit 1; }

# Saída dos targets de QA (banco, bug report, captura de tela e vídeo)
QA_DIR ?= qa
DB_NAME := leasily_app_db
DB ?= $(QA_DIR)/$(DB_NAME).db

# $(call pull_db,<arquivo>) copia o banco do app para o host num único arquivo; falha se o resultado
# não for um SQLite (app não instalado, não debuggable ou sem banco ainda).
# O Room usa WAL: as escritas recentes podem estar só no -wal, então ele vem junto e é consolidado no host
pull_db = ( \
	_pd=$$(mktemp -d); \
	$(QA_ADB) exec-out run-as $(QA_PACKAGE) cat databases/$(DB_NAME) > $$_pd/db; \
	if $(QA_ADB) shell run-as $(QA_PACKAGE) test -f databases/$(DB_NAME)-wal; then \
		$(QA_ADB) exec-out run-as $(QA_PACKAGE) cat databases/$(DB_NAME)-wal > $$_pd/db-wal; \
	fi; \
	if head -c 15 $$_pd/db | grep -q "SQLite format 3" && sqlite3 $$_pd/db "PRAGMA wal_checkpoint(TRUNCATE);" > /dev/null; then \
		mv $$_pd/db $(1); rm -rf $$_pd; \
	else \
		rm -rf $$_pd; false; \
	fi \
)
# $(call push_db,<arquivo>) sobrescreve o banco do app com o arquivo do host; apaga -wal e -shm
# para o Room não reaplicar um WAL antigo por cima
push_db = $(QA_ADB) push -q $(1) /data/local/tmp/leasily_qa.db && \
	$(QA_ADB) shell "run-as $(QA_PACKAGE) cp /data/local/tmp/leasily_qa.db databases/$(DB_NAME) && run-as $(QA_PACKAGE) rm -f databases/$(DB_NAME)-wal databases/$(DB_NAME)-shm && rm /data/local/tmp/leasily_qa.db"

# ========================================================

# Instala o app (flavor dev, debug) nos aparelhos conectados e o abre no aparelho DEVICE
run:
	./gradlew :app:installDevDebug
	@$(REQUIRE_DEVICE); \
	$(QA_ADB) shell am start -n $(QA_PACKAGE)/com.fstengineering.leasilycrm.MainActivity > /dev/null

apk-release:
	./gradlew assembleRelease
	./gradlew bundleRelease

test:
	./gradlew :app:testDevDebugUnitTest

test-instrumented:
	./gradlew :app:connectedDevDebugAndroidTest

# Alterna "Não manter atividades" (Opções do desenvolvedor) no aparelho, ex.: make dont-keep-activities DEVICE=2
dont-keep-activities:
	@set -e; $(REQUIRE_DEVICE); \
	if [ "$$($(QA_ADB) shell settings get global always_finish_activities | tr -d '\r')" = "1" ]; then value=0; label=desativado; else value=1; label=ativado; fi; \
	$(QA_ADB) shell settings put global always_finish_activities $$value; \
	echo "Não manter atividades $$label em $(QA_SERIAL)"

# Salva o banco do app no host para snapshot de cenário (para o app antes), ex.: make pull-db DB=qa/estadia-em-andamento.db
pull-db:
	@set -e; $(REQUIRE_DEVICE); \
	mkdir -p $$(dirname $(DB)); \
	$(QA_ADB) shell am force-stop $(QA_PACKAGE); \
	$(call pull_db,$(DB)) || { rm -f $(DB); echo "não foi possível ler o $(DB_NAME) de $(QA_PACKAGE) em $(QA_SERIAL)"; exit 1; }; \
	echo "$(DB_NAME) de $(QA_PACKAGE) ($(QA_SERIAL)) salvo em $(DB)"

# Restaura no app um banco salvo com pull-db (para o app antes), ex.: make push-db DB=qa/estadia-em-andamento.db
# O snapshot só vale para o mesmo schema: o Room recusa banco com identity hash diferente
push-db:
	@set -e; $(REQUIRE_DEVICE); \
	test -f $(DB) || { echo "arquivo $(DB) não encontrado"; exit 1; }; \
	$(QA_ADB) shell am force-stop $(QA_PACKAGE); \
	$(call push_db,$(DB)) || exit 1; \
	echo "$(DB) restaurado em $(QA_PACKAGE) ($(QA_SERIAL))"

# Alterna o Wi-Fi do aparelho, ex.: make wifi DEVICE=2
wifi:
	@set -e; $(REQUIRE_DEVICE); \
	if $(QA_ADB) shell dumpsys wifi | grep -q "Wi-Fi is enabled"; then state=disable; label=desativado; else state=enable; label=ativado; fi; \
	$(QA_ADB) shell svc wifi $$state; \
	echo "Wi-Fi $$label em $(QA_SERIAL)"

# Alterna os dados móveis do aparelho, ex.: make mobile-data DEVICE=2
mobile-data:
	@set -e; $(REQUIRE_DEVICE); \
	if [ "$$($(QA_ADB) shell settings get global mobile_data | tr -d '\r')" = "1" ]; then state=disable; label=desativados; else state=enable; label=ativados; fi; \
	$(QA_ADB) shell svc data $$state; \
	echo "Dados móveis $$label em $(QA_SERIAL)"

# Alterna o tema escuro do aparelho, ex.: make dark-mode DEVICE=2
dark-mode:
	@set -e; $(REQUIRE_DEVICE); \
	if $(QA_ADB) shell cmd uimode night | grep -q "yes"; then state=no; label=desativado; else state=yes; label=ativado; fi; \
	$(QA_ADB) shell cmd uimode night $$state > /dev/null; \
	echo "Dark mode $$label em $(QA_SERIAL)"

# Tags de framework/lib silenciadas no logcat-app.txt: ruído sem relação com bugs do app
LOGCAT_APP_SILENCED_TAGS := '[Koin]:S' FrameTracker:S ImeTracker:S InsetsController:S WindowOnBackDispatcher:S \
	HWUI:S CompatChangeReporter:S GraphicsEnvironment:S

# Gera $(QA_DIR)/bug-report-<timestamp>.zip com logcat, crashes, dumpsys package (versão, permissões...),
# configuração do aparelho (tela, fonte, idioma, tema, teclado) e banco do app, sem parar o app. Ex.: make bug-report DEVICE=2
# logcat-app.txt traz só o log do app (por UID, sobrevive a restart pós-crash), sem o ruído do resto do aparelho
bug-report:
	@set -e; $(REQUIRE_DEVICE); \
	ts=$$(date +%Y%m%d-%H%M%S); tmp=$$(mktemp -d); mkdir -p $(QA_DIR); \
	$(QA_ADB) logcat -b crash -d > $$tmp/crash.txt; \
	$(QA_ADB) shell dumpsys package $(QA_PACKAGE) > $$tmp/dumpsys-package.txt; \
	uid=$$(sed -n 's/.*appId=\([0-9]*\).*/\1/p' $$tmp/dumpsys-package.txt | head -1); \
	if [ -n "$$uid" ]; then \
		$(QA_ADB) logcat -d --uid=$$uid $(LOGCAT_APP_SILENCED_TAGS) > $$tmp/logcat-app.txt; \
	else echo "aviso: logcat-app.txt não gerado ($(QA_PACKAGE) não instalado)"; fi; \
	{ \
		echo "# Aparelho (versão do app: versionName em dumpsys-package.txt)"; \
		echo "Android $$($(QA_ADB) shell getprop ro.build.version.release) (API $$($(QA_ADB) shell getprop ro.build.version.sdk)), $$($(QA_ADB) shell getprop ro.product.model)"; \
		echo; echo "# Idioma, tamanho do app (sw/w/h em dp), densidade, orientação (port/land), tema (night/notnight) e teclado (nokeys/qwerty...)"; \
		$(QA_ADB) shell am get-config | head -1; \
		echo; echo "# Tamanho da tela e densidade (Override = tamanho de exibição alterado nas configurações)"; \
		$(QA_ADB) shell wm size; \
		$(QA_ADB) shell wm density; \
		echo; echo "# Tamanho e peso da fonte (null = padrão)"; \
		echo "font_scale: $$($(QA_ADB) shell settings get system font_scale)"; \
		echo "font_weight_adjustment: $$($(QA_ADB) shell settings get secure font_weight_adjustment)"; \
		echo; echo "# Dark mode"; \
		$(QA_ADB) shell cmd uimode night; \
		echo; echo "# Idioma do app ([] = idioma do sistema; só Android 13+)"; \
		$(QA_ADB) shell cmd locale get-app-locales $(QA_PACKAGE) || true; \
	} > $$tmp/config.txt; \
	$(call pull_db,$$tmp/$(DB_NAME).db) || echo "aviso: $(DB_NAME) de $(QA_PACKAGE) não incluído"; \
	zip -qj $(QA_DIR)/bug-report-$$ts.zip $$tmp/*; \
	rm -rf $$tmp; \
	echo "Bug report salvo em $(QA_DIR)/bug-report-$$ts.zip"

# Salva a tela do aparelho em $(QA_DIR)/screenshot-<timestamp>.png, ex.: make screenshot DEVICE=2
# Com mais de um display (ex.: AVD Resizable), captura o ativo; sem -d o screencap mistura um aviso no PNG
screenshot:
	@set -e; $(REQUIRE_DEVICE); \
	ts=$$(date +%Y%m%d-%H%M%S); mkdir -p $(QA_DIR); \
	display=$$($(QA_ADB) shell dumpsys display | sed -n "s/.*isActive=true, displayId=0, uniqueId='local:\([0-9]*\)'.*/\1/p" | head -1); \
	$(QA_ADB) exec-out screencap -p $${display:+-d $$display} > $(QA_DIR)/screenshot-$$ts.png; \
	[ "$$(head -c 4 $(QA_DIR)/screenshot-$$ts.png | tail -c 3)" = PNG ] || { echo "captura inválida em $(QA_DIR)/screenshot-$$ts.png"; exit 1; }; \
	echo "Captura de tela salva em $(QA_DIR)/screenshot-$$ts.png"

# Grava a tela do aparelho até Enter (limite do Android: 3 min) em $(QA_DIR)/video-<timestamp>.mp4, ex.: make video DEVICE=2
video:
	@set -e; $(REQUIRE_DEVICE); \
	ts=$$(date +%Y%m%d-%H%M%S); mkdir -p $(QA_DIR); \
	$(QA_ADB) shell screenrecord /sdcard/qa_video.mp4 < /dev/null & pid=$$!; \
	echo "Gravando o $(QA_SERIAL)... pressione Enter para parar"; read _; \
	$(QA_ADB) shell pkill -INT screenrecord || true; \
	wait $$pid || true; \
	$(QA_ADB) pull -q /sdcard/qa_video.mp4 $(QA_DIR)/video-$$ts.mp4; \
	$(QA_ADB) shell rm /sdcard/qa_video.mp4; \
	echo "Vídeo salvo em $(QA_DIR)/video-$$ts.mp4"

# Limpa o storage dos apps dev e prod no aparelho; ignora o pacote não instalado
# e só falha se nenhum estiver. Ex.: make clear-data DEVICE=2
clear-data:
	@set -e; $(REQUIRE_DEVICE); \
	cleared=0; \
	for pkg in $(APP_PACKAGES); do \
		if $(QA_ADB) shell pm path $$pkg >/dev/null 2>&1; then \
			$(QA_ADB) shell pm clear $$pkg >/dev/null; \
			echo "Storage de $$pkg limpo em $(QA_SERIAL)"; \
			cleared=$$((cleared + 1)); \
		fi; \
	done; \
	test $$cleared -gt 0 || { echo "nenhum pacote ($(APP_PACKAGES)) instalado em $(QA_SERIAL)"; exit 1; }

# Permissões de runtime que o app declara, lidas do dumpsys (variam com a API do aparelho)
RUNTIME_PERMS = $(QA_ADB) shell dumpsys package $(QA_PACKAGE) | awk '/runtime permissions:/ {f=1; next} f && /granted=/ {sub(/:$$/, "", $$1); print $$1; next} {f=0}' | sort -u

# Concede todas as permissões de runtime do app (notificações), ex.: make grant-perms DEVICE=2
grant-perms:
	@set -e; $(REQUIRE_DEVICE); \
	for perm in $$($(RUNTIME_PERMS)); do $(QA_ADB) shell pm grant $(QA_PACKAGE) $$perm; done; \
	echo "Permissões concedidas a $(QA_PACKAGE) em $(QA_SERIAL)"

# Revoga todas as permissões de runtime do app; o Android mata o processo junto. Ex.: make revoke-perms DEVICE=2
revoke-perms:
	@set -e; $(REQUIRE_DEVICE); \
	for perm in $$($(RUNTIME_PERMS)); do $(QA_ADB) shell pm revoke $(QA_PACKAGE) $$perm; done; \
	echo "Permissões revogadas de $(QA_PACKAGE) em $(QA_SERIAL)"

# Simula a morte do processo pelo SO para liberar memória: manda o app para background e o mata;
# reabra pelos recentes para testar a restauração de estado. Ex.: make kill DEVICE=2
kill:
	@set -e; $(REQUIRE_DEVICE); \
	$(QA_ADB) shell input keyevent KEYCODE_HOME; \
	sleep 1; \
	$(QA_ADB) shell am kill $(QA_PACKAGE); \
	if $(QA_ADB) shell pidof $(QA_PACKAGE) > /dev/null; then echo "$(QA_PACKAGE) segue vivo em $(QA_SERIAL) (tá com serviço em primeiro plano?)"; exit 1; fi; \
	echo "Processo de $(QA_PACKAGE) morto em $(QA_SERIAL); reabra o app pelos Recentes"
