# `kotlin-parcelize` para models de presentation/

Models de apresentação que precisam sobreviver a morte de processo (guardados em
`SavedStateHandle`) devem ser `Parcelable` via plugin `kotlin-parcelize` (`@Parcelize`) — o
container `UiState` e seus sub-models. O plugin já está aplicado em `app/build.gradle.kts`.

## Motivação

`@Parcelize` gera `writeToParcel`/`CREATOR` a partir do construtor primário — zero chance de
descasar ordem de escrita/leitura, suporte nativo a enums, `data class`, `object`, coleções e
aninhamento. É o formato que `SavedStateHandle.getStateFlow(KEY, valorInicial)` aceita
diretamente, permitindo usar o próprio `SavedStateHandle` como fonte do `uiState` público
(`0007-presentation-jetpack-compose-mvvm.md`).

## Alternativas Consideradas

- **`Parcelable` manual**: boilerplate repetitivo e classe de bug real (ordem de campos, ordinais
  de enum reordenados corrompendo o parcel em silêncio).
- **`java.io.Serializable`**: funciona no `SavedStateHandle`, mas é reflexivo, mais lento, sem
  contrato explícito de campos, e desencorajado no Android.
- **`kotlinx.serialization`** (já usado nas rotas de navegação e DTOs remotos): exigiria a API de
  saved state serializável em vez do `getStateFlow` nativo do `SavedStateHandle`; mantém-se restrito
  a navegação e rede.

## Consequências

- `@Parcelize` exige que todo campo seja `Parcelable`/primitivo/`@Parcelize` — aninhar um model não
  parcelável não compila, o que puxa a anotação em cascata para os sub-models do `UiState`.
- Models de `application/`/`domain/` não ganham `@Parcelize`: a anotação é detalhe de
  `presentation/`, e o `UiState` mapeia a partir deles.
