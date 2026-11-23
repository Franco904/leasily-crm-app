# Nomes

De "A Philosophy of Software Design" (Ousterhout), capítulo 14 ("Choosing Names"):

Um bom nome cria na mente do leitor uma imagem do que a entidade é. Um nome mal escolhido obriga o
leitor a abrir a implementação ou o leva a supor errado. Quando o conceito é de domínio, o nome é o
termo do `GLOSSARY.md`. Fora do domínio, o nome é em inglês (ver [Idioma](#idioma)). A escada
nome → comentário está na [0012](0012-comments.md#nome-antes-de-comentário).

## Precisão

O nome diz o que a entidade é, sem deixar lacuna para a suposição do leitor. Um nome **vago**
(`data`, `result`, `status`, `flag`) não descreve a entidade. Nome de uma letra existe só no
conjunto fechado `i`, `j`, `k` (índice de loop), `e` (exceção ou erro capturado) e `v` (valor).
Se nenhum nome preciso cabe, a entidade não tem um propósito único. Nesse caso, volte à
[0010](0010-interface-design.md).

```kotlin
// RUIM
fun getStatus(): Int

// BOM
fun getEstadiaStatus(): EstadiaStatus
```

## Consistência

Cada conceito tem um único nome, e cada nome tem um único conceito. Dois nomes para o mesmo conceito
são **sinônimos**, e isso inclui um termo diferente do usado no `GLOSSARY.md`. Um nome usado para
dois conceitos é **ambíguo**.

```kotlin
// RUIM: sinônimo, Locatário e Hóspede para o mesmo conceito
suspend fun saveHospede(hospede: Cliente)
suspend fun getLocatarioEstadias(locatarioId: String): List<Estadia>

// BOM
suspend fun saveHospede(hospede: Cliente)
suspend fun getHospedeEstadias(hospedeId: String): List<Estadia>
```

## Palavras extras

Toda palavra no nome acrescenta informação. Detalhes de mais são ruído para os chamadores.

```kotlin
// RUIM
val imovelData: Imovel
class ImovelServiceImpl : ImovelService // única implementação

// BOM
val imovel: Imovel
```

Em `application/` e `domain/`, o nome declarado não leva `Manager`, `Helper`, `DataService`,
`Json`, `Validator`, nem o sufixo `Data` ou `Info`: o termo do glossário já diz o que a entidade
é. `Impl` não existe em nenhuma camada. `presentation/` e `infra/` nomeiam tipos da plataforma e
ficam fora da lista (`CredentialManager`, `WorkManager`).

## Idioma

Nomes de variável, método, parâmetro e classe/interface usam **inglês**, exceto quando o nome é
(ou incorpora) um termo definido em `GLOSSARY.md`: esse termo permanece em português, palavra
por palavra, sem acento e sem preposição.

```kotlin
// RUIM
fun getStay(): Estadia                  // tradução do termo de domínio
fun temArquivoDeImagem(): Boolean       // português para o que é genérico
val taxaDeLimpeza: Money                // preposição do glossário ("Taxa de limpeza")

// BOM
fun getEstadia(): Estadia
fun hasPictureFile(): Boolean
val taxaLimpeza: Money
```

Termo com preposição vira camelCase sem ela: `OrigemContato`, `ValorPendenteCheckIn`,
`StatusEstadia`. As preposições são `de`, `da`, `do`, `das`, `dos`, `em`, `no`, `na`, `nos`,
`nas`, `para` e `com`. Os `_Alias de código_` do `GLOSSARY.md` são legado: código novo usa o termo
em português.

Vale em todos os source sets: `app/src/main/`, `app/src/test/` e `app/src/androidTest/`, exceto
a descrição do teste, que é em português com preposição, conforme a
[0002](0002-all-tests.md#1-nomenclatura-e-idioma).

## Nome simples

Todo tipo é referenciado pelo nome simples, com `import` no topo. Nome totalmente qualificado no
corpo é proibido. Conflito de nome se resolve com alias de import.

```kotlin
// RUIM
val ids: kotlinx.coroutines.flow.Flow<Long> = watchIds()
val settings = android.provider.Settings.Global.getString(resolver, name)

// BOM
import android.provider.Settings as AndroidSettings

val ids: Flow<Long> = watchIds()
val settings = AndroidSettings.Global.getString(resolver, name)
```

## Red flags

| Red flag                                            | Severidade                                |
|-----------------------------------------------------|-------------------------------------------|
| Nome ambíguo ([Consistência](#consistência))        | `[must fix]`                              |
| Nome sinônimo ([Consistência](#consistência))       | `[should fix]`                            |
| Nome vago ([Precisão](#precisão))                   | `[should fix]`                            |
| Idioma errado ([Idioma](#idioma))                   | `[should fix]`                            |
| Nome qualificado no corpo ([Nome simples](#nome-simples)) | `[should fix]`                |
| Palavra extra ([Palavras extras](#palavras-extras)) | `[nitpick]`                               |
| Nome difícil de escolher ([Precisão](#precisão))    | code-smell de design, arrumar ao escrever |
