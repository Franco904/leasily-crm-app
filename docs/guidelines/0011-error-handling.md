# Tratamento de Erros

De "A Philosophy of Software Design" (Ousterhout), capítulo "Define errors out of existence":

Cada lugar que precisa tratar um erro é complexidade que vaza para a interface e se multiplica pelos
chamadores. O objetivo geral é **reduzir o número de lugares onde erros precisam ser tratados**.

Isso vale para **todo** erro — exceção genuína ou caminho infeliz. Um `AppResult<T>` cujo lado de
erro poderia não existir também obriga N chamadores a tratá-lo.

## A escada

Aplique em ordem. Desça para o próximo passo só quando o atual não couber.

### 1. Definir fora da existência

Redefina a semântica da operação para que o caso deixe de ser erro. Some o gatilho, some o
tratamento em todos os chamadores.

```kotlin
// Antes: devolve Error(EstadiaNaoEncontrada) e todo chamador precisa ramificar, embora o estado
// final desejado já valha
suspend fun deleteEstadia(estadiaId: String): AppResult<Unit>

// Depois: idempotente — "Estadia não existe" ao final, tenha existido antes ou não. Só uma falha de
// infra volta como erro (passo 3)
suspend fun deleteEstadia(estadiaId: String): AppResult<Unit>
```

> **Critério**: só redefina se o estado final redefinido estiver correto do ponto de vista de
> **todos** os chamadores ("a Estadia não existe" ✓). Se a redefinição depende de adivinhar intenção
> ("o chamador provavelmente quis X"), ela esconde bugs do chamador — desça para o próximo passo.

Value Objects que absorvem a entrada inválida são a mesma ideia aplicada ao domínio (ver
[0008](0008-domain-aggregates-services.md#erros-de-domínio)).

### 2. Mascarar

O módulo detecta e trata a falha em nível baixo, sem expô-la ao chamador. É legítimo apenas quando
os três itens valem:

- **(a) Promessa da interface cumprida**: depois do handler, o chamador recebe exatamente o que a
  interface prometeu (via retry, fallback para cache válido, recomputação, valor padrão previsto).
- **(b) Falha observável**: a falha é registrada em log/telemetria (`ExceptionLogger`), mesmo sem
  aparecer na assinatura.
- **(c) Degradação declarada**: o comentário de **interface** (KDoc, ver
  [0012](0012-comments.md#comentário-de-interface)) descreve quando e para quê o resultado degrada.
  Comentário de implementação não conta — o chamador não o enxerga.

```kotlin
/**
 * Fotos do Imóvel para a galeria. Foto cujo download do Storage falha é substituída pelo
 * placeholder local, sem interromper as demais. Cada falha é registrada em telemetria.
 */
suspend fun getImovelFotos(imovelId: String): List<ImovelFotoReadModel>
```

Faltando qualquer item, não é mascaramento — é **engolir o erro**: o chamador acredita que a operação
foi cumprida quando não foi. Sinais típicos: `(result as? AppResult.Success)?.data ?: <valor>`,
`onError = { <valor> }` ou `catch { log(e) }` sem degradação declarada na interface.

> **Linha do banco com campo obrigatório nulo** é dado vindo de sistema externo, não invariante
> violado (passo 4): o mapper de `infra/` retorna `null` e o DataSource descarta (`.mapNotNull`) ou
> degrada o item. Isso é mascaramento — vale só com (b) e (c).

### 3. Agregar

Quando o erro precisa chegar a alguém, trate muitos pontos de falha em **um único lugar**, em vez de
espalhar handlers.

Dentro do aplicativo ou na fronteira com o mundo externo, o mecanismo padrão de propagação é um tipo
de retorno de erro (`AppResult`) — em contraste com `try-catch-finally`, que em excesso impacta
desempenho de processamento e quebra linearidade do código. O chamador de alto nível é o agregador:
um único `.fold()` decide o que fazer.

```kotlin
// interface
suspend fun getEstadia(estadiaId: String): AppResult<EstadiaDetailsReadModel>

// chamador
result.fold(
    onError = { error -> _uiState.update { it.copy(error = error.toUiError()) } },
    onSuccess = { estadia -> _uiState.update { it.copy(estadia = estadia.toUiModel()) } },
)
```

`throw` é aceitável apenas quando **nasce e morre dentro da mesma camada**: bibliotecas de terceiros
lançam, e **um único handler agregador na borda** de `infra/` captura, registra e converte em
retorno de erro. Nenhum `throw` cruza para `domain/`, `application/` ou `presentation/`.

```kotlin
// agregador de borda (core/infra/utils/BoundaryUtils.kt) — todo acesso ao mundo externo passa por aqui
inline fun <T> onDatabase(block: () -> T): AppResult<T> {
    try {
        return AppResult.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (e: Exception) {
        if (e.isFatal()) throw e // ver passo 4
        ExceptionLogger.log(e)
        return AppResult.Error(ApplicationError.Unknown)
    }
}

// DataSource — nenhum try/catch próprio
suspend fun getEstadia(estadiaId: String): AppResult<EstadiaOrm?> =
    onDatabase { queryEstadia(estadiaId) }
```

O protocolo da borda (relançar `CancellationException` e as fatais do passo 4, registrar no
`ExceptionLogger`, converter) existe **uma vez só**, em `BoundaryUtils.kt`. `catch (e: Exception)`
genérico mora só lá; fora dele, capture o subtipo específico da biblioteca ou use um wrapper. O
wrapper escolhe o degrau: agregar devolve `AppResult.Error(ApplicationError.Unknown)` (este passo);
mascarar registra e devolve o valor de fallback, com a degradação declarada no KDoc de interface
(passo 2).

#### Princípio: Não distinga o que nenhum chamador distingue

Cada tipo de erro é parte da interface. Uma hierarquia (`Timeout` / `NoInternet` /
`InvalidPayload`) cujo único chamador trata todos os casos igual é granularidade sem consumidor:
colapse-a. Vale tanto para hierarquias de `throw` dentro da camada quanto para o lado de erro do
retorno (ver [Modelando o lado de erro do retorno](#modelando-o-lado-de-erro-do-retorno)).

#### Princípio: Exceções apenas para o que é excepcional

De "O Programador Pragmático" (Hunt & Thomas), uma forma de detectar quando um `throw` é usado
indevidamente como fluxo de controle:

> **Teste da exclusão**: Imagine excluir o **manipulador** (`try`/`catch`). Se o código, no caminho
> normal, continuar completando sua tarefa principal sem nunca precisar daquele `catch`, ele era só
> uma rede de segurança para uma falha externa/inesperada — uso correto de exceção. Se remover o
> `catch` impede o código de completar sua tarefa **no fluxo normal** (ex: decidir um resultado de
> negócio, quebrar um laço, desviar fluxo), o `catch` era parte da lógica de controle disfarçada de
> tratamento de exceção — uso incorreto: volte ao passo 1 ou modele como retorno de erro.

### 4. Deixar crashar

Quando não há recuperação significativa, não trate.

> **Critério**: crashar é obrigatório quando continuar pode **produzir ou propagar estado
> corrompido** — um invariante de domínio foi violado. Perder a operação em andamento é preferível a
> persistir ou sincronizar dado errado.

- Invariantes de domínio e aplicação são verificados com `check`/`require` e **nunca** viram retorno
  de erro nem são mascarados. Regra de negócio esperada não é invariante: volta como `DomainError`
  ([0008](0008-domain-aggregates-services.md#erros-de-domínio)).
- O `block` do agregador de borda (`onDatabase { }`, `asDatabaseFlowResult()`, …) contém **apenas**
  a conversa com o mundo externo (e mapeamento mecânico do que voltou). Lógica de domínio —
  validação, cálculo, decisão — dentro dele teria seus bugs convertidos em `Unknown` e escondidos do
  monitoramento de estabilidade.
- `check`/`require` sobre o dado que acabou de voltar do mundo externo pode ficar dentro do `block`
  quando o crash não resolveria o problema (reiniciar o processo não corrige o dado ou o ambiente que
  causou a violação): o agregador o converte em erro (passo 3). Quando o crash resolve (ex.:
  `OutOfMemoryError`), nada o captura e o processo cai (passo 4).

```kotlin
// BOM: borda só fala com o mundo externo; agregado (e seus check/require) fora dela
onDatabase { queryEstadia(estadiaId) }
    .fold(
        onError = { error -> AppResult.Error(error) },
        onSuccess = { orm -> orm.toEstadia().registerCheckIn(now) },
    )

// RUIM: invariante violado dentro do agregado vira "Erro ao carregar" em vez de crash
onDatabase {
    queryEstadia(estadiaId).toEstadia().registerCheckIn(now)
}
```

> **Nota: nunca capture o que sinaliza estado irrecuperável do processo ou do ambiente:**
>
> - **Erros da VM** (`OutOfMemoryError`, `StackOverflowError`): são `Throwable`, não `Exception` —
>   nunca capture `Throwable`/`Error`. Cuidado com `runCatching`, que captura `Throwable` (inclusive
>   `CancellationException`).
> - **Permissão negada pelo SO** (`SecurityException`): a permissão é verificada antes da chamada
>   (passo 1). Se ainda assim lança, é bug no fluxo de verificação.
> - **Falhas de criptografia** (`GeneralSecurityException`, ex: tag de autenticação AES-GCM inválida
>   ao decifrar o Token de sessão): dado adulterado ou chave errada — continuar é operar sobre dado
>   não confiável. Exceção: `KeyPermanentlyInvalidatedException` (invalidação de chave do Keystore
>   prevista pela plataforma, ex: após troca do bloqueio de tela) é evento esperado — trate pela
>   escada (recriar a chave e exigir novo login).
>
> Permissão e criptografia são subtipos de `Exception`, então o `catch (e: Exception)` do agregador
> de borda as capturaria. O agregador as relança explicitamente — e quem embrulha a exceção de
> biblioteca (ex.: `CryptoUtils`) preserva o tipo original para que a checagem funcione:
>
> ```kotlin
> fun Exception.isFatal(): Boolean =
>     this is SecurityException ||
>         (this is GeneralSecurityException && this !is KeyPermanentlyInvalidatedException)
> ```

## Modelando o lado de erro do retorno

`AppResult<D>` tem **um só parâmetro**: `Success(data)` ou `Error(error: BaseError)`. O tipo do
erro não aparece na assinatura; quem precisa distinguir ramifica sobre o valor com `when`/`is`
(`BaseError` é `sealed`, então o `when` é exaustivo; liste os subtipos em vez de usar `else`,
para o compilador avisar quando surgir um novo). A UI deve tratar todo erro de infra igual (e.g. `BaseError.toBottomSheetData`).

Os erros vivem num catálogo central em `core/domain/utils/` (kernel,
[ADR-0001](../adr/0001-core-shared-kernel.md)): caso novo entra como `enum` aninhado em
`DomainError` (regra de domínio) ou como membro de `ApplicationError` (falha de infra), ambos
`BaseError`, e pode assumir três formas — escolha pela necessidade do chamador, não por padrão fixo:

| Forma                         | Quando usar                                                                                 | Exemplo                                       |
|-------------------------------|---------------------------------------------------------------------------------------------|-----------------------------------------------|
| `enum class`                  | Caso padrão — não carrega dado associado                                                    | `ApplicationError.Unknown`                    |
| `data class`                  | Erro carrega dado estruturado associado ao erro                                             | `ValidationErrors(errors)`                    |
| Hierarquia `sealed interface` | Chamador precisa ramificar por subtipo para decidir fluxo (ex: abortar tudo vs. só um item) | `DomainError.StayPeriod` / `DomainError.Name` |

Nenhuma forma carrega mensagem nem causa técnica — essas vão para o `ExceptionLogger`
([0009](0009-infra-repositories-data-sources.md)).

Pattern-matching sobre o erro dentro de um `.fold(onError = { error -> … }, …)`
(`when (error) { ApplicationError.Unauthorized -> … }`) é o uso correto — a ramificação acontece
sobre o valor de erro, não sobre o objeto do `throw` ou bloco `catch`.
