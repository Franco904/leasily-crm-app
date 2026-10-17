Leasily CRM
===========

Aplicativo mobile Android para automação de processos de CRM especializado na intermediação de
locações de imóveis residenciais por temporada, no domínio de Administração Imobiliária.
Público-alvo: corretores de imóveis autônomos. Funciona no modelo offline-first, sincronizando
oportunisticamente com o back-end. Ver [`GLOSSARY.md`](GLOSSARY.md) para o glossário da linguagem
ubíqua e o mapa dos contextos delimitados do domínio.

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Banco local:** SQLite (Room)
- **Back-end:** Ktor/MySQL (cliente Ktor)
- **Autenticação:** federada via Google (OAuth 2.0)
- **Injeção de dependência:** Koin
- **Build:** Gradle (Android Studio), flavors `dev` e `prod`

---

## Configuração

1. Clone o repositório:
   ```
   git clone https://github.com/Franco904/leasily-crm-app.git
   ```
2. Abra no Android Studio e instancie um emulador de dispositivo Android
3. Rode no terminal, na raiz do repositório: `make run`. O .apk da branch default (`main`) será
   gerado e instalado no dispositivo para uso

---

## Estrutura do Projeto

Módulos são packages dentro de `app/src/main/java/com/fstengineering/leasilycrm/`. Os nomes em
inglês são legado: a migração para os termos do [`GLOSSARY.md`](GLOSSARY.md) em português está
planejada (ex.: `occupiedPeriodRegistration` → `periodoOcupadoRegistration`).

| Módulo                       | Contexto delimitado   | Responsabilidade                                                                                                                                        |
|------------------------------|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `core`                       | Compartilhado         | Shared kernel (ver [ADR-0001](docs/adr/0001-core-shared-kernel.md)): erros, Value Objects, utilitários de fronteira e artefatos usados por 2+ contextos |
| `root`                       | —                     | Inicialização e configuração do app                                                                                                                     |
| `home`                       | — (shell)             | Tela inicial: resumo do perfil, contagem de Leads, check-ins/check-outs do dia e navegação                                                              |
| `settings`                   | —                     | Configurações do app: notificações de check-in/check-out e preferências                                                                                 |
| `signin`                     | Autenticação          | Login via Google e token de sessão                                                                                                                      |
| `profile`                    | Autenticação          | Configuração de perfil pós-login: Creci e dados profissionais                                                                                           |
| `properties`                 | Imóveis               | Listagem de Imóveis                                                                                                                                     |
| `propertyRegistration`       | Imóveis               | Cadastro e edição de Imóveis                                                                                                                            |
| `propertyDetails`            | Imóveis               | Detalhes de um Imóvel (abas: identificação, financeiro)                                                                                                 |
| `propertyDashboard`          | Imóveis               | Visão centralizada de um Imóvel                                                                                                                         |
| `propertyOccupation`         | Imóveis               | Calendário de ocupação do Imóvel                                                                                                                        |
| `propertyOwners`             | Proprietários         | Listagem de Proprietários                                                                                                                               |
| `propertyOwnerRegistration`  | Proprietários         | Cadastro e edição de Proprietários                                                                                                                      |
| `propertyOwnerDetails`       | Proprietários         | Detalhes de um Proprietário                                                                                                                             |
| `leadTenants`                | Clientes              | Listagem de Clientes segmentada por estado (Leads e Inquilinos)                                                                                         |
| `leadTenantRegistration`     | Clientes              | Cadastro e edição de Clientes                                                                                                                           |
| `leadTenantDetails`          | Clientes              | SCV: visão centralizada do Cliente; exclusão de Atendimento e Cliente                                                                                   |
| `assistanceRegistration`     | Atendimento           | Cadastro e gestão de Atendimentos                                                                                                                       |
| `occupiedPeriodRegistration` | Períodos Ocupados     | Cadastro de Estadias e Períodos indisponíveis                                                                                                           |
| `occupiedPeriods`            | Períodos Ocupados     | Listagem de Períodos Ocupados com filtros (status e subtipo)                                                                                            |
| `occupiedPeriodDetails`      | Períodos Ocupados     | Detalhes de Período Ocupado; check-in e check-out                                                                                                       |
| `dashboard`                  | Relatórios Analíticos | KPIs e relatórios de desempenho do Usuário                                                                                                              |

Testes vivem em `app/src/test/` (unitários: JUnit 5 + MockK + kluent) e `app/src/androidTest/`
(instrumentados: JUnit 4 + kluent, requerem device).

---

## Arquitetura

MVVM + Jetpack Compose na apresentação, com a lógica separada nas camadas `presentation/`,
`application/`, `domain/` e `infra/`. Kotlin Coroutines para concorrência. Classes concretas por
padrão — interface só com polimorfismo de runtime.

As convenções de cada camada — responsabilidades, dependências permitidas, nomenclatura — vivem em
`docs/guidelines/`:

| Camada          | Guideline                                                                                                |
|-----------------|----------------------------------------------------------------------------------------------------------|
| `presentation/` | [`0006-presentation-jetpack-compose-mvvm.md`](docs/guidelines/0006-presentation-jetpack-compose-mvvm.md) |
| `application/`  | [`0007-application-use-cases.md`](docs/guidelines/0007-application-use-cases.md)                         |
| `domain/`       | [`0008-domain-aggregates-services.md`](docs/guidelines/0008-domain-aggregates-services.md)               |
| `infra/`        | [`0009-infra-repositories-data-sources.md`](docs/guidelines/0009-infra-repositories-data-sources.md)     |

Quais camadas um módulo precisa depende da classificação do seu subdomínio
([`0001-subdomains.md`](docs/guidelines/0001-subdomains.md)).

---

## Documentação do Projeto (`docs/`)

A leitura de [`GLOSSARY.md`](GLOSSARY.md) e de `docs/` é **obrigatória e recorrente** — antes de
implementar uma tarefa e antes de revisar um PR, não apenas no onboarding. Ela evolui; manter-se
atualizado evita desvios durante a codificação.

- **[`GLOSSARY.md`](GLOSSARY.md)**: glossário da linguagem ubíqua e mapa dos contextos delimitados
  (Autenticação, Imóveis, Clientes, Proprietários, Atendimento, Períodos Ocupados, Relatórios
  Analíticos, Sincronização).
- **[`docs/adr/`](docs/adr/)**: decisões arquiteturais já tomadas e o porquê de cada uma — evita
  reabrir discussões já fechadas.
- **[`docs/guidelines/`](docs/guidelines/)**: diretrizes e padrões táticos — classificação de
  subdomínios, convenções de teste (comuns, unitário, integração, apresentação), convenções de cada
  camada, design de interface, tratamento de erros, comentários e nomes.

---

## Comandos Essenciais

```bash
./gradlew :app:assembleDevDebug && ./gradlew :app:installDevDebug   # build APK debug (flavor dev) + instala no device/emulador conectado
./gradlew :app:testDevDebugUnitTest                                 # testes unitários (JUnit 5, sem device)
./gradlew :app:testDevDebugUnitTest --tests "*.NomeTest"            # teste unitário específico
./gradlew :app:connectedDevDebugAndroidTest                         # testes instrumentados (requer device)
make sign-app                                                       # build + bundle release assinados
./gradlew signingReport                                             # verifica a assinatura
```
