#!/usr/bin/env bash
# Formata o layout (indentação e quebra em 100 colunas, via .editorconfig) com o formatador do
# Android Studio. Sem argumentos, formata só os .kt do diff da branch (scripts/ratchet-base.sh),
# incluindo alterações não commitadas e arquivos novos; com argumentos, formata os arquivos dados.
#
# Roda numa instância isolada da IDE (config, system, plugins e log em diretórios descartáveis),
# então funciona com o Android Studio aberto e não toca nas configurações do usuário.
#
# O Studio é localizado por $STUDIO_BIN (caminho do `format.sh` da distribuição), por
# $STUDIO_HOME/bin/format.sh ou pelos caminhos padrão de macOS e Linux. Sem Studio, sai com código 3 e não formata nada.
set -euo pipefail

readonly EXIT_SEM_STUDIO=3

root="$(git rev-parse --show-toplevel)"
cd "$root"

find_studio() {
    if [ -n "${STUDIO_BIN:-}" ]; then
        [ -x "$STUDIO_BIN" ] && echo "$STUDIO_BIN"
        return 0
    fi
    local candidate
    for candidate in \
        "$HOME/Applications/Android Studio.app/Contents/bin/format.sh" \
        "/Applications/Android Studio.app/Contents/bin/format.sh" \
        "${STUDIO_HOME:-/nonexistent}/bin/format.sh" \
        "/opt/android-studio/bin/format.sh" \
        "$HOME/android-studio/bin/format.sh"; do
        if [ -x "$candidate" ]; then
            echo "$candidate"
            return 0
        fi
    done
}

studio="$(find_studio)"
if [ -z "$studio" ]; then
    echo "format.sh: Android Studio não encontrado; aponte STUDIO_BIN para o format.sh da distribuição" >&2
    exit "$EXIT_SEM_STUDIO"
fi

files=()
if [ "$#" -gt 0 ]; then
    for file in "$@"; do
        files+=("$file")
    done
else
    base="$("$root/scripts/ratchet-base.sh")"
    while IFS= read -r file; do
        files+=("$file")
    done < <({
        git diff --name-only --diff-filter=d "$base" -- ':(glob)app/src/**/*.kt'
        git ls-files --others --exclude-standard -- ':(glob)app/src/**/*.kt'
    } | sort -u)
fi

if [ "${#files[@]}" -eq 0 ]; then
    exit 0
fi

sandbox="$(mktemp -d)"
trap 'rm -rf "$sandbox"' EXIT
cat > "$sandbox/studio.properties" <<EOF
idea.config.path=$sandbox/config
idea.system.path=$sandbox/system
idea.plugins.path=$sandbox/plugins
idea.log.path=$sandbox/log
EOF

STUDIO_PROPERTIES="$sandbox/studio.properties" "$studio" -r -m "*.kt" "${files[@]}" > "$sandbox/format.log" 2>&1 || {
    cat "$sandbox/format.log" >&2
    exit 1
}
