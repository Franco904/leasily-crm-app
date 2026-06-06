---
name: to-issues
description: Deriva um épico em issues independentes no GitHub usando fatias verticais (projéteis luminosos). Use quando o usuário quiser converter um épico em issues, criar tickets de implementação, ou decompor o trabalho em histórias de usuário e tarefas.
---

## Processo

### 1. Coletar contexto

Trabalhe a partir do que já está no contexto da conversa. Se o usuário passar uma referência de
issue (número, URL ou caminho) como argumento, busque-a no GitHub e leia o corpo completo e os
comentários.

### 2. Explorar a base de código (opcional)

Se você ainda não explorou a base de código, faça isso para entender o estado atual do código.
Títulos e descrições de issues devem usar o vocabulário do glossário de domínio do projeto (
`CONTEXT.md`) e respeitar quaisquer ADRs na área que você está tocando.

### 3. Rascunhar as fatias verticais

Decomponha o épico em issues do tipo **projétil luminoso**. Cada issue é uma fatia vertical fina que
corta e "ilumina" TODAS as camadas de integração de ponta a ponta (ex.: presentation, domain, data,
external) — NÃO uma fatia horizontal de uma única camada.

As issues podem ser classificadas como **HITL** (*Human In The Loop*) ou **AFK** (*Away From
Keyboard*). Issues HITL requerem interação humana, como uma decisão arquitetural ou uma revisão de
design. Issues AFK podem ser implementadas e mergeadas sem interação humana. Prefira AFK sempre que
possível.

Use de guia de decisão para o tipo da issue:

- **História de usuário**: descreve uma solução de um problema do ponto de vista do especialista de
  domínio;
- **Tarefa**: descreve uma ação pontual e específica, complementar às histórias, ou atende a uma
  necessidade técnica identificada (ex: dívida técnica, configuração de infraestrutura).

Use histórias de usuário para a maioria dos casos.

<vertical-slice-rules>
- Cada fatia entrega um caminho estreito, mas COMPLETO por todas as camadas relevantes (UI, casos de uso, lógica de negócio, dados)
- Uma fatia concluída é demonstrável ou verificável por conta própria
- Prefira muitas fatias finas a poucas fatias grossas
</vertical-slice-rules>

### 4. Validar com o usuário

Apresente a decomposição proposta como uma lista numerada. Para cada fatia, mostre:

- **Título**: nome descritivo e curto
- **Tipo**: História de Usuário / Tarefa
- **HITL / AFK**
- **Bloqueada por**: quais outras issues precisam ser concluídas antes (se houver)

Pergunte ao usuário:

- A granularidade parece adequada? (muito grossa / muito fina)
- As dependências entre issues estão corretas?
- Alguma issue deve ser mesclada ou dividida?
- As classificações HITL e AFK estão corretas?

Itere até o usuário aprovar a decomposição.

### 5. Entrega

Para cada fatia aprovada, crie um novo arquivo .md na raiz do repositório em `issues/`. Use o
template de issue abaixo. Organize as issues em ordem de dependência (bloqueadoras primeiro), para
que você possa referenciar identificadores reais no campo "Dependências".

NÃO feche nem modifique a issue pai (épico). Também não inclua a seção "Épico pai", essa ligação
será feita manualmente pelo(a) PO no GitHub depois.

---

<issue-template>

### Descrição

Uma descrição concisa desta fatia vertical. Descreva o comportamento de ponta a ponta, não a
implementação camada por camada.

Evite caminhos de arquivo específicos ou trechos de código — eles ficam desatualizados rapidamente.
Exceção: se um plano produziu um trecho que codifica uma decisão com mais precisão do que a prosa
consegue (máquina de estados, reducer, schema, formato de tipo), incorpore-o aqui e note brevemente
que veio de um plano. Reduza às partes ricas em decisão — não uma demo funcional, apenas os trechos
importantes.

Se se tratar de uma história de usuário, use o formato:

1. Como <ator>, eu quero <funcionalidade>, para que <benefício>.

<user-story-example>
1. Como cliente de banco, eu quero ver o saldo das minhas contas, para que eu possa tomar decisões mais bem informadas sobre meus gastos
</user-story-example>

### Critérios de Aceitação

- [ ] Critério 1
- [ ] Critério 2

#### Bônus

1.
    - [ ] Bônus (subseção opcional)

### Não Escopo

Uma descrição das coisas que estão fora do escopo desta issue.

### Documentação

[Inserir link da documentação]

### Protótipo

[Inserir link do protótipo]

### Decisões de Implementação

Uma lista de decisões de implementação que foram tomadas. Pode incluir:

- Os módulos que serão construídos/modificados
- As interfaces desses módulos que serão modificadas
- Esclarecimentos técnicos do desenvolvedor
- Decisões arquiteturais
- Mudanças de schema
- Contratos de API
- Interações específicas

NÃO inclua caminhos de arquivo específicos ou trechos de código. Eles podem ficar desatualizados
rapidamente.
Exceção: se um plano produziu um trecho que codifica uma decisão com mais precisão do que a prosa
consegue (máquina de estados, reducer, schema, formato de tipo), incorpore-o dentro da decisão
relevante e note brevemente que veio de um plano. Reduza às partes ricas em decisão — não uma demo
funcional, apenas os trechos importantes.

### Decisões de Teste

Uma lista de decisões de teste que foram tomadas. Inclua:

- Uma descrição do que torna um teste bom (teste apenas comportamento externo de negócio, não
  detalhes de implementação)
- Quais módulos serão testados
- Estado da arte para os testes (ou seja, tipos similares de testes na base de código)

### Branch

`feature/nome-da-feature`

### Referência

[Inserir link da referência]

### Dependências

- Referência à issue bloqueadora (se houver)

Ou "Nenhuma — pode iniciar imediatamente" se não houver bloqueadores.

</issue-template>
