# Leasily CRM — Guia do Agente de IA

Ver [README.md](../README.md) para visão geral do app, stack, arquitetura, estrutura do projeto e
comandos essenciais. Este arquivo cobre apenas o que é específico de como um agente de IA deve
trabalhar neste repositório.

---

## Linguagem Ubíqua

**Obrigatório:** use os termos definidos em `CONTEXT.md` para nomear classes, métodos, variáveis,
módulos e descrições de teste. Nunca invente sinônimos, abreviações não listadas ou termos técnicos
da indústria (ex.: `Manager`, `Helper`, `DataService`).

Exemplos corretos:

- Entidades/estados: `Imovel`, `Proprietario`, `Lead`, `Inquilino`, `InquilinoAntigo`,
  `Atendimento`, `PeriodoOcupado`, `Estadia`, `PeriodoIndisponivel`
- Eventos: `CheckIn`, `CheckOut`
- Use cases: `RegisterCheckInUseCase`, `RegisterCheckOutUseCase`, `UndoRegisterStayCheckOutUseCase`
- Erros: `AppResult.Error(DomainError.OverbookingDetected)`, nunca `throw DomainException(...)`

**Inglês por padrão fora do domínio:** nomes de variável, método, parâmetro e classe/interface usam
**inglês**, exceto quando o nome é (ou incorpora) um termo definido em `CONTEXT.md` — esse termo
permanece em português, verbatim, sem acento (ex.: `EstadiaDao`, `getEstadia`,
`WriteEstadiaUseCase`;
não `StayDao`/`getStay`). Descrições de teste (`@Test fun`) continuam em português
(`docs/guidelines/0002-all-tests.md`). O código legado ainda usa os termos traduzidos (`Stay`,
`Property`, `LeadTenant`) — o alias de cada termo está no `CONTEXT.md`; código novo segue a regra.
Vale também para nomes de módulo, feature, tela e ViewModel, sem exceção (ex.:
`periodoOcupadoRegistration/`, `PeriodoOcupadoRegistrationViewModel`, `clienteDetails/`).

---

## Convenções de Nomenclatura

| Artefato                                | Sufixo / padrão                                                                                                                              |
|-----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| Caso de uso                             | `<VerboInglês><TermoDeDomínio>UseCase` (ex: `RegisterCheckOutUseCase`, `WriteEstadiaUseCase`)                                                |
| Repositório (write-side)                | `<Contexto>Repository` (classe concreta, sem `Impl`)                                                                                         |
| DataSource                              | `<Contexto>WriteDataSource` / `<Contexto>ReadDataSource` (classe concreta, sem `Impl`) / DAO: `<Contexto>Dao`                                |
| Erro                                    | `AppResult` com `DomainError`/`ValidationErrors` (regra de domínio) ou `ApplicationError` (falha de infra), todos `BaseError`; nunca `throw` |
| ViewModel                               | `<Contexto>ViewModel`                                                                                                                        |
| UiState                                 | `<Contexto>UiState`                                                                                                                          |
| Modelo de UI (genérico de presentation) | `<Contexto>UiModel`                                                                                                                          |
| Composable (tela)                       | `<Contexto>Screen`                                                                                                                           |
| Composable (componente)                 | `<Contexto>` ou nome descritivo sem sufixo                                                                                                   |
| Arquivo de teste                        | `<ArquivoProducao>Test.kt` (ex: `PeriodoOcupadoRegistrationViewModelTest.kt`)                                                                |

---

## Plans

- Faça do plano algo extremamente conciso. Priorize concisão em detrimento de gramática.
- Ao final, tenha um olhar crítico em relação ao plano. Procure por eventuais inconsistências e
  identifique complexidades acidentais, sinalizando-as ao usuário para tomada de decisão.
- Também sinalize quaisquer questões não respondidas para o usuário responder, se houver.

---

## Commits e PRs

- **Idioma:** sempre em português.
- **Coautoria proibida:** nunca incluir `Co-Authored-By` (nem qualquer atribuição de autoria a IA)
  nas mensagens de commit.
- **Tipos de commit:** prefixe a mensagem com o tipo, ex: `fix: sobreposição de períodos na
  validação de overbooking`.
    - `feat`: nova funcionalidade ou melhoria em existente
    - `fix`: correção de bug
    - `refactor`: mesma funcionalidade, apenas melhoria de código/manutenção
    - `build`: afeta arquivos de build
    - `chore`: afeta arquivos auxiliares secundários (ex: lint, scripts)
    - `test`: testes automatizados de qualquer tipo/nível
    - `doc`: documentação markdown/html
- **Descrição:** extremamente concisa. Priorize concisão em detrimento de gramática.
- **Atomicidade:** cada commit deve conter uma única mudança logicamente coesa.

---

## Rules

Antes de agir, **sempre** consulte as rules do projeto e balize suas decisões por elas. Diretrizes
de camada e de teste ficam em `docs/guidelines/` — consulte a da camada/nível que a tarefa toca.

| Arquivo                             | Conteúdo                                                                                                                 |
|-------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| @CONTEXT.md                         | Glossário da linguagem ubíqua e mapa dos contextos delimitados — base para nomes de classes, métodos, variáveis e testes |
| @docs/guidelines/0001-subdomains.md | Classificação estratégica dos subdomínios e orientação para padrões táticos de DDD                                       |
| `docs/adr/`                         | ADRs com decisões de arquitetura do sistema; não questione, apenas siga                                                  |
| `docs/guidelines/`                  | Testes (0002–0005), design de interface (0006) e camadas presentation/application/domain/infra (0007–0010)               |

---

## Skills

| Comando               | Descrição                                                                                                                                                     |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/grill-me`           | Confronta um plano de discovery ou delivery com o modelo de domínio, aprimora terminologia e atualiza `CONTEXT.md` e ADRs conforme as decisões se cristalizam |
| `/to-epic`            | Transforma o contexto da conversa atual em um épico — sintetiza requisitos sem entrevistar o usuário. Invoque diretamente após `/grill-me`                    |
| `/to-issues`          | Deriva um épico em issues independentes no GitHub usando fatias verticais (tracer bullets). Invoque diretamente após `/to-epic`                               |
| `/tdd`                | Desenvolvimento orientado a testes (red-green-refactor) — use na fase de implementação (funcionalidades ou correção de bugs) com TDD, após a fase de plano    |
| `/to-pr`              | Gera o corpo do PR seguindo o template do projeto, com plano de QA ponta-a-ponta derivado do tracer bullet da issue                                           |
| `/review-code`        | Revisa código como arquiteto sênior seguindo os padrões e diretrizes do projeto                                                                               |
| `/handoff`            | Compacta a conversa atual em documento de handoff para outro agente continuar                                                                                 |
| `/teach`              | Ensina o usuário sobre um tópico dentro do workspace, ao longo de múltiplas sessões                                                                           |
| `/writing-for-agents` | Referência para escrever skills, `CLAUDE.md` e outros documentos consumidos por agentes — consulte antes de editar este arquivo                               |
