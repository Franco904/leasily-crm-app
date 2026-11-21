# Comentários

De "A Philosophy of Software Design" (Ousterhout), capítulos sobre comentários (12, 13, 15 e 16):

Um comentário diz ao leitor o que o código não consegue dizer. O objetivo é reduzir, para o
chamador, dois sintomas de complexidade: a **carga cognitiva** (quanto ele precisa saber para usar o
módulo) e os **desconhecidos desconhecidos** (o que ele precisa saber e só descobriria lendo a
implementação).

## Nome antes de comentário

A escada é: primeiro o nome, depois o comentário. Se o comentário só existe para explicar o que um
identificador (variável, parâmetro, função, classe) significa, renomeie o identificador e remova o
comentário. Quando o conceito é de domínio, o nome vem do `GLOSSARY.md` (ver [0013](0013-naming.md)).
Isso evita ambiguidade, sinônimos e suposições do chamador. O comentário fica só com o que nenhum
nome curto consegue carregar de maneira concisa e óbvia.

```kotlin
// RUIM: comentário compensa o nome
// noites entre check-in e check-out
val n: Int

// BOM
val nightsBetweenCheckInAndCheckOut: Int
```

## Comentário de interface

Usa KDoc e fica logo acima da declaração pública. Descreve a parte da interface (definida na
[0010](0010-interface-design.md)) que a assinatura não mostra.

É obrigatório quando ao menos um _gatilho_ se aplica:

- significado de `null`, de um booleano ou de um valor sentinela;
- degradação ou mascaramento (ver [0011, passo 2](0011-error-handling.md#2-mascarar));
- efeito colateral não óbvio pelo nome;
- pré-condição ou garantia de que o chamador depende (idempotência, thread-safety, pureza,
  determinismo);
- metainformação de valor, seja de parâmetro ou de retorno (unidade de medida, precisão decimal,
  fuso horário);
- o porquê de uma escolha que surpreenderia o chamador.

Quando nenhum gatilho se aplica, a declaração fica sem comentário.

```kotlin
// BOM: o chamador conhece cada degradação sem abrir o corpo

/**
 * Fotos do Imóvel para a galeria, com a foto principal primeiro.
 *
 * Foto cujo download do Storage falha é substituída pelo placeholder local, sem interromper as
 * demais. Sem foto principal marcada, a primeira por data de upload assume o lugar.
 */
suspend fun getImovelFotos(imovelId: String): List<ImovelFotoReadModel>

// RUIM: degradações no corpo, invisíveis ao chamador

suspend fun getImovelFotos(imovelId: String): List<ImovelFotoReadModel> {
    val fotos = imovelFotoReadDataSource.getFotos(imovelId)

    // sem foto principal, usa a mais antiga
    val principal = fotos.firstOrNull { it.isMain } ?: fotos.minByOrNull { it.uploadedAt }

    // download falho vira placeholder
    return fotos.map { foto -> download(foto) ?: placeholder(foto) }
    …
}
```

## Comentário de implementação

Usa comentário de linha ou de bloco dentro do corpo, logo acima do trecho, e explica **o quê** (o
propósito de um bloco, em nível mais alto que o código) ou **por quê** (garantia preservada,
alternativa descartada, armadilha da biblioteca/plataforma). O contrato (significado de `null`,
degradação, efeito colateral) vai para o comentário de interface, porque o chamador não lê o corpo.

```kotlin
// Grava o Imóvel e a linha de outbox das fotos na mesma transação: se o processo morrer entre as
// duas escritas, o upload pendente nunca fica órfão (ADR 0003).
@Transaction
protected open suspend fun saveInTransaction(imovel: ImovelOrm, outbox: List<OutboxOrm>) { … }
```

## Teste da exclusão

- **Exclua o comentário.** Se o chamador não perde nenhuma informação que já não estivesse na
  assinatura ou no código, o comentário era repetição. Remova-o.
- **Exclua o corpo.** Se, vendo só a assinatura e o comentário de interface, o chamador ainda
  precisaria abrir a implementação para usar o método corretamente, falta um gatilho no comentário.
  Isso é um desconhecido desconhecido.

```kotlin
// RUIM: repete a assinatura e omite o que o chamador precisa saber
/** Retorna a Estadia. */
suspend fun getEstadia(estadiaId: String): EstadiaDetailsReadModel?

// BOM
/** Estadia com Hóspede e Imóvel resolvidos. `null` quando a Estadia foi excluída. */
suspend fun getEstadia(estadiaId: String): EstadiaDetailsReadModel?
```

Se o comentário de interface precisa descrever a implementação para ser entendido, o módulo está
raso. Ver [0010](0010-interface-design.md#deep-modules-x-shallow-modules).

## Comentário primeiro

Escreva o comentário de interface junto com a assinatura, antes da implementação. Se ele não sai
curto, o problema está na interface: volte à [0010](0010-interface-design.md).

## Manutenção

- **O código é a fonte da verdade.** Todo comentário, de interface ou de implementação, vive no
  código. Repetir a explicação no commit ou na PR é aceitável. Deixá-la só lá, não.
- **Proximidade.** O comentário fica logo acima do código objeto que ele descreve.
- **Fonte única.** Cada explicação fica em um único lugar do código. Quando não há lugar óbvio, como
  uma decisão que cruza módulos, ela vai para um ADR em `docs/adr/`.
- **O comentário acompanha o código.** Um diff que muda um fato documentado atualiza o comentário de
  interface no mesmo commit, evitando mentir aos chamadores.
- **TODO não é comentário.** Trabalho pendente vai para a lista de comportamentos da `tdd` ou para
  uma issue de débito técnico, nunca para o código.
- **Antes do commit**, o diff passa no Teste da exclusão: toda declaração pública com gatilho tem
  comentário de interface, todo trecho cujo o quê ou por quê não é óbvio tem comentário de
  implementação, e não há TODO.

## Red flags

| Red flag                                                                                                    | Severidade                                |
|-------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| Comentário de interface desatualizado ([Manutenção](#manutenção))                                           | `[must fix]`                              |
| Degradação ausente ou fora do comentário de interface ([Comentário de interface](#comentário-de-interface)) | `[must fix]`                              |
| Outro gatilho sem comentário de interface ([Comentário de interface](#comentário-de-interface))             | `[should fix]`                            |
| Contrato em comentário de implementação ([Comentário de implementação](#comentário-de-implementação))       | `[should fix]`                            |
| Detalhe de implementação em comentário de interface ([Teste da exclusão](#teste-da-exclusão))               | `[should fix]`                            |
| Nome explicado por comentário ([Nome antes de comentário](#nome-antes-de-comentário))                       | `[should fix]`                            |
| TODO no diff ([Manutenção](#manutenção))                                                                    | `[should fix]`                            |
| Explicação só no commit ou na PR ([Manutenção](#manutenção))                                                | `[should fix]`                            |
| Comentário que repete a assinatura ou o código ([Teste da exclusão](#teste-da-exclusão))                    | `[nitpick]`                               |
| Comentário difícil de escrever ([Comentário primeiro](#comentário-primeiro))                                | code-smell de design, arrumar ao escrever |
