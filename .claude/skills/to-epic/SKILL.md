---
name: to-epic
description: Transforma o contexto da conversa atual em um épico. NÃO entreviste o usuário — apenas sintetize o que você já sabe. Use quando o usuário quiser criar um épico ou documento de requisitos a partir do contexto atual.
---

## Processo

1. Explore o repositório para entender o estado atual da base de código, caso ainda não o tenha
   feito. Use os termos do glossário de domínio do produto (`GLOSSARY.md`) ao longo do épico e
   respeite quaisquer ADRs na área que você está tocando.
2. Prepare o terreno para derivação em histórias de usuário ou tarefas. Use de guia de decisão: *
   *história de usuário** para descrever uma solução de um problema do ponto de vista do
   especialista de domínio; **tarefa** para descrever uma ação pontual e específica, complementar às
   histórias, ou atender a uma necessidade técnica identificada, como uma dívida técnica. Use
   histórias de usuário para a maioria dos casos.
3. Escreva o épico usando o template abaixo e crie uma nova issue para ele no issue tracker (
   GitHub).

---

<epic-template>

### Descrição

Um resumo do problema de negócio que o usuário está enfrentando.

### Solução

A solução para o problema, especificando a motivação e objetivos na perspectiva do usuário.

### Não Escopo

Uma descrição das coisas que estão fora do escopo deste épico.

### Notas Adicionais

Quaisquer notas adicionais sobre a funcionalidade.

</epic-template>
