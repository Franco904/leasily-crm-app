# Leasily CRM — Guia do Agente de IA

## Visão Geral

Protótipo de aplicativo mobile Android para automação de processos de CRM especializado na intermediação de locações de imóveis residenciais por temporada, no domínio de Administração Imobiliária. Público-alvo: corretores de imóveis autônomos. Funciona no modelo offline-first (SQLite local + sincronização oportunística com back-end Ktor/MySQL). Autenticação federada via Google (OAuth 2.0). UI com Jetpack Compose e Material Design 3.

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Banco local:** SQLite
- **Back-end client:** Ktor

---

## Estrutura do Projeto

Módulos são packages dentro de `app/src/main/java/com/fstengineering/leasilycrm/`. Os nomes dos módulos seguem convenção Kotlin (inglês); a coluna "Contexto" indica o mapeamento para o domínio em português.

| Módulo | Contexto delimitado | Responsabilidade |
|---|---|---|
| `app` | — | Ponto de entrada do projeto |
| `core` | Compartilhado | Value objects, erros (`AppResult`, `DomainError`, `BusinessRuleError`), serviços de domínio transversais e use cases consumidos por múltiplos módulos |
| `root` | — | Inicialização e configuração do app |
| `home` | — (shell) | Tela inicial: resumo do perfil, contagem de Leads, check-ins/check-outs do dia e navegação |
| `settings` | — | Configurações do app: notificações de check-in/check-out e preferências |
| `signin` | Autenticação | Login via Google e token de sessão |
| `profile` | Autenticação | Configuração de perfil pós-login: Creci e dados profissionais |
| `properties` | Imóveis | Listagem de Imóveis |
| `propertyRegistration` | Imóveis | Cadastro e edição de Imóveis |
| `propertyDetails` | Imóveis | Detalhes de um Imóvel (abas: identificação, financeiro) |
| `propertyDashboard` | Imóveis | Visão centralizada de um Imóvel |
| `propertyOccupation` | Imóveis | Calendário de ocupação do Imóvel |
| `propertyOwners` | Clientes — Proprietários | Listagem de Proprietários |
| `propertyOwnerRegistration` | Clientes — Proprietários | Cadastro e edição de Proprietários |
| `propertyOwnerDetails` | Clientes — Proprietários | Detalhes de um Proprietário |
| `leadTenants` | Clientes — Lead/Inquilino | Listagem de Clientes segmentada por estado (Leads e Inquilinos) |
| `leadTenantRegistration` | Clientes — Lead/Inquilino | Cadastro e edição de Clientes |
| `leadTenantDetails` | Clientes — Lead/Inquilino | SCV: visão centralizada do Cliente; exclusão de Atendimento e Cliente |
| `assistanceRegistration` | Atendimento | Cadastro e gestão de Atendimentos |
| `occupiedPeriodRegistration` | Períodos Ocupados | Cadastro de Estadias e Períodos indisponíveis |
| `occupiedPeriods` | Períodos Ocupados | Listagem de Períodos Ocupados com filtros (status e subtipo) |
| `occupiedPeriodDetails` | Períodos Ocupados | Detalhes de Período Ocupado; check-in e check-out |
| `dashboard` | Relatórios Analíticos | KPIs e relatórios de desempenho do Usuário corretor (`userAnalytics` é o nome de domínio pretendido) |

A estrutura de pastas de cada módulo varia conforme o padrão de lógica de negócio aplicado:

- **Modelo de Domínio** (subdomínios Core): `presentation/application/domain/infra` — Clean Architecture completa; inversão de dependências obrigatória.
- **Script de Transação** (subdomínios Suporte ou módulos sem lógica de domínio): `presentation/application/infra`, `presentation/infra` ou apenas `presentation/` — grau de separação proporcional à complexidade real da orquestração.

---

## Arquitetura

### Orientação ao Domínio

Em módulos com Modelo de Domínio (subdomínios Core), a dependência flui de fora para dentro: `infra → application → domain ← application ← presentation`.

- **domain**: objetos Kotlin puros. Sem dependência de frameworks ou infraestrutura. Contém as regras de negócio.
- **application**: orquestra casos de uso; define interfaces de repositório consumidas por `infra`.
- **infra**: implementa as interfaces de `application`; encosta em bibliotecas de terceiros (Room, Ktor, DataStore).
- **presentation**: ViewModel + Jetpack Compose (Unidirectional Data Flow).

---

## Linguagem Ubíqua

**Obrigatório:** use os termos definidos em `CONTEXT.md` para nomear classes, métodos, variáveis, módulos e descrições de teste. Nunca invente sinônimos, abreviações não listadas ou termos técnicos da indústria (ex.: `Manager`, `Helper`, `DataService`).

Exemplos corretos:
- Entidades/estados: `Imovel`, `Proprietario`, `Lead`, `Inquilino`, `InquilinoAntigo`, `Atendimento`, `PeriodoOcupado`, `Estadia`, `PeriodoIndisponivel`
- Eventos: `CheckIn`, `CheckOut`
- Use cases: `RegisterCheckInUseCase`, `RegisterCheckOutUseCase`, `UndoRegisterStayCheckOutUseCase`
- Erros: `AppResult.Error(DomainError.OverbookingDetected)`, nunca `throw DomainException(...)`

---

## Convenções de Nomenclatura

| Artefato | Sufixo / padrão |
|---|---|
| Caso de uso | `<Acao><Contexto>UseCase` (ex: `RegisterCheckOutUseCase`) |
| Repositório (interface) | `<Contexto>Repository` |
| Repositório (impl) | `<Contexto>RepositoryImpl` |
| Datasource (interface) | `<Contexto>Datasource` |
| Datasource (impl) | `<Contexto>DatasourceImpl` / DAO: `<Contexto>Dao` |
| Erro de domínio | `AppResult<T, DomainError>` — use `DomainError` para erros de domínio e `BusinessRuleError` para violações de regra de negócio; não use enums de exceção |
| ViewModel | `<Contexto>ViewModel` |
| UiState | `<Contexto>UiState` |
| Modelo de UI (genérico de presentation) | `<Contexto>UiModel` |
| Composable (tela) | `<Contexto>Screen` |
| Composable (componente) | `<Contexto>` ou nome descritivo sem sufixo |
| Arquivo de teste | `<arquivo_produção>Test.kt` |

---

## Comandos Essenciais

```bash
# Gerar assinatura do aplicativo
make sign-app

# Verificar assinatura
./gradlew signingReport

# Testes
./gradlew :app:test
./gradlew :app:test --tests "*.NomeTest"

# Geração de código (ksp/kapt)
./gradlew kspDebugKotlin
```

> ⚠️ Comandos aproximados — confirmar Makefile e tasks Gradle disponíveis ao explorar o projeto.

---

### Plans

- Faça do plano algo extramamente conciso. Priorize concisão em detrimento de gramática.
- Ao final, tenha um olhar crítico em relação ao plano. Procure por eventuais inconsistências e identifique complexidades acidentais, sinalizando-as ao usuário para tomada de decisão.
- Também sinalize quaisquer questões não respondidas para o usuário responder, se houver.

---

## Rules

Consulte antes de agir — especialmente os de teste, aplicados **somente quando a tarefa envolver testes**.

| Arquivo                                            | Conteúdo                                                                                                                 |
|----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `CONTEXT.md`                                       | Glossário da linguagem ubíqua e mapa dos contextos delimitados — base para nomes de classes, métodos, variáveis e testes |
| `docs/adr`                                         | Caminho para os ADRs com decisões de arquitetura do sistema; não questione, apenas siga                                  |
| `.claude/rules/1_subdomains.md`                    | Classificação estratégica dos subdomínios e orientação para adoção de padrões táticos de DDD                             |
| `.claude/rules/2_unit_tests_guidelines.md`         | Padrões de testes unitários                                                                                              |
| `.claude/rules/3_integration_tests_guidelines.md`  | Padrões de testes de integração                                                                                          |
| `.claude/rules/4_presentation_tests_guidelines.md` | Padrões de testes de ViewModel/UiState/UiModel                                                                           |

---

## Skills

| Comando | Descrição                                                                                                                                               |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/grill-with-docs` | Confronta um plano com o modelo de domínio, aprimora terminologia e atualiza `CONTEXT.md` e ADRs conforme as decisões se cristalizam                    |
| `/tdd` | Desenvolvimento orientado a testes (red-green-refactor) — use na fase de implementação (funcionalidades ou correção de bugs) com TDD, após a fase de plano |
| `/handoff` | Compacta a conversa atual em documento de handoff para outro agente continuar                          |
