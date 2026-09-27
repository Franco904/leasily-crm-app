# Camada `domain/` — agregados e serviços de domínio

Convenções para a camada `domain/`: o núcleo isolado de regra de negócio.

## Isolamento total

- `domain/` **não importa nada** de `application/`, `infra/`, `presentation/`, nem
  de framework Android, ORM, coroutines ou I/O.
- Dependências só **entram**: de `application/` e de `presentation/` (caso de validações parciais
  quando há perda de foco ou edição de form field). O domínio nunca alcança nada para fora — se um agregado precisa
  de um dado externo, o UseCase busca e passa como parâmetro.
- Sem `suspend`, sem acesso a framework de UI, sem anotação de persistência, sem `String` de UI.
- Só subdomínio **Principal** justifica modelo de domínio rico. Subdomínios de Suporte podem adotar
  script de transação (UseCase/ViewModel → `infra/`) sem agregado/`domain/`.

## Agregado

- **Único lugar onde invariante de negócio vive.**
- **Construído por factory validante** (`companion object parse(...)` e
  construtor privado). A factory rejeita estado inválido com exceção de
  domínio tipada — nunca devolve um agregado meio-válido. Validação de
  invariante de negócio em mapper, UseCase, ViewModel ou DataSource é proibido: a invariante
  tem um dono só.
- **Comportamento no agregado**, não num objeto utilitário anêmico ao lado dele (e.g. `Validator`).
  Métodos nomeiam operações do domínio; a mutação de estado passa por eles.
- **Fronteira de consistência**: 1 agregado = 1 fronteira transacional. Referência
  a outro agregado é por identidade, não por objeto aninhado.
- Agregado vs. Entidade não tem marcador no código — a distinção é pacote +
  termo do `CONTEXT.md`. O agregado é a raiz que o resto da app referencia e que o
  `Repository` persiste (`0010-infra-repositories-data-sources.md`).
- **Nome = termo do `CONTEXT.md`, sem sufixo de tipo.** O agregado é o **único**
  model sem sufixo — `ReadModel`/`CommandModel`/`QueryModel`/`Orm`/`UiState`
  todos carregam o seu (`0008-application-use-cases.md`, "Sufixo de tipo no nome
  do model"). A ausência de sufixo é o que marca "isto é o modelo do domínio, não
  um DTO de fronteira".

## Value Object

- Tipo imutável sem identidade, definido pelo valor (e.g. `Cpf`, `Creci`,
  `Diaria`). Valida o próprio formato/faixa na construção (factory `parse(...)`),
  como o agregado.
- **Reutilizável fora do agregado**: pode ser usado em `presentation/` para validar um campo
  isoladamente antes de existir agregado (e.g. por form field, formulário em etapas).
  Não arrasta o resto do domínio junto.

## Domain Service

- Extrair **apenas quando** a lógica: (1) é regra de domínio e (2) não cabe em
  nenhum agregado sozinho.
- Serve tanto o write-side quanto o **read-side**: quando uma transformação
  complexa do `ReadModel` é lógica de domínio genuína (não formatação de tela,
  não responsabilidade do SQL), um Domain Service a executa — acionado por um
  UseCase de leitura (`0008-application-use-cases.md`).
- Se envolve I/O, autorização de acesso, permissão de conectividade ou orquestração de persistência → não é
  serviço de domínio, é UseCase ou Application Service (`0008-application-use-cases.md`).
- Também é puro e isolado — mesmas restrições do agregado.

## Erros de domínio

- **Nunca `throw`.** Retornados como valor: `AppResult<Agregado, ValidationErrors>` na factory
  (`ValidationErrors(errors: ImmutableList<DomainError>)` acumula todas as violações do
  formulário) e `AppResult<T, DomainError>` no comportamento do agregado. `DomainError`,
  `ValidationErrors` e `ApplicationError` herdam de `BaseError` (`sealed interface`,
  `core/domain/utils/`) — a UI faz `when` exaustivo sobre o erro.
- Antes de criar um caso de erro, tente defini-lo fora da existência — um tipo que não admite
  o valor inválido elimina o erro e o teste dele. Ex.: `Quantidade.of(n)` trunca em 0; `Amount` descarta o sinal com
  `abs()` — nenhum dos dois devolve erro.
- Carregam dado estruturado suficiente para a apresentação traduzir em mensagem.
  A tradução para texto de UI é de `presentation/`, não do domínio.

## Sem porta de saída no domínio

O domínio **não declara interface de Repository/DataSource**. A persistência do agregado
é responsabilidade de `infra/` (Repository concreto, `0010-infra-repositories-data-sources.md`),
acionada a partir de `application/`.
