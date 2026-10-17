# Outbox para escrita em serviço remoto

Escrita que precisa chegar a um serviço remoto (Firebase Storage, back-end Ktor) não é feita em
sequência com a escrita local (dual-write). O `WriteDataSource` grava o agregado e uma linha
`Pending` numa tabela de outbox na mesma transação do Room; um worker do WorkManager chama o
`<Agregado>SyncRepository`, que envia ao serviço remoto e marca a linha como `Consumed`. O envio
usa chave remota determinística derivada da linha de outbox, de modo que reenviar após uma falha
entre o envio e a marcação sobrescreve o mesmo objeto em vez de duplicá-lo.

## Alternativas Consideradas

- **Dual-write via Repository** (SQL e serviço remoto em sequência no fluxo do usuário): a segunda
  escrita falha offline — cenário normal num app offline-first — deixando local e remoto
  inconsistentes sem registro do que faltou enviar.
- **Status `InFlight` para evitar reenvio**: adiciona um estado e a recuperação de linhas presas
  nele; a chave determinística torna o reenvio inofensivo e elimina esse estado.

## Consequências

- O Repository só existe no lado do worker, onde coordena de fato duas tecnologias
  (`0009-infra-repositories-data-sources.md`).
- O fluxo do usuário conclui com a escrita local; o dado remoto fica eventualmente consistente.
