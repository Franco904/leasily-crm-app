---
name: grill-with-docs
description: Sessão de questionamento que confronta seu plano com o modelo de domínio existente, aprimora a terminologia e atualiza a documentação (CONTEXT.md, ADRs) conforme as decisões se cristalizam. Use quando o usuário quiser testar um plano sob pressão em relação à linguagem e às decisões documentadas do projeto.
---

<what-to-do>
Interrogue-me sem trégua sobre cada aspecto deste plano até chegarmos a um entendimento compartilhado. Percorra cada ramificação da árvore de decisão, resolvendo as dependências entre as decisões uma a uma. Para cada pergunta, forneça sua resposta recomendada.
Faça as perguntas uma de cada vez, aguardando o retorno de cada uma antes de continuar.
Se uma pergunta puder ser respondida explorando a base de código, explore a base de código em vez de perguntar.
</what-to-do>

<supporting-info>

## Consciência de domínio

Durante a exploração da base de código, procure também por documentação existente:

### Estrutura de arquivos

Mapeamento de contexto(s) e ADRs:

```
/
├── CONTEXT.md
├── docs/
│   └── adr/
│       ├── 0001-event-sourced-orders.md
│       └── 0002-postgres-for-write-model.md
└── modules/
```

Crie arquivos de forma preguiçosa — somente quando tiver algo a escrever. Se não existir um
`CONTEXT.md`, crie um quando o primeiro termo for resolvido. Se não existir `docs/adr/`, crie quando
o primeiro ADR for necessário.

## Durante a sessão

### Confronte com o glossário

Quando o usuário usar um termo que conflite com a linguagem ubíqua existente no `CONTEXT.md`, aponte
imediatamente. "Seu glossário define 'cancelamento' como X, mas você parece querer dizer Y — qual é
o correto?"

### Afine a linguagem imprecisa

Quando o usuário usar termos vagos, sinônimos ou ambíguos, proponha um termo canônico preciso. "Você
está dizendo 'conta' — você quer dizer o Cliente ou o Usuário? São coisas diferentes."

### Respeite os limites de contexto

Quando o usuário sugerir uma mudança que cruza um limite de contexto, questione a decisão.

- **Testes não cruzam contextos:** testes de um módulo não importam lógica de negócio interna de
  outro módulo. Use apenas interfaces públicas ou fakes no núcleo compartilhado.
- **Núcleo não é atalho:** adicionar algo no núcleo compartilhado só se justifica por uso
  genuinamente transversal — não por conveniência. Não centralize componentes sem validar com o
  usuário antes.

### Discuta cenários concretos

Quando relações de domínio estiverem sendo discutidas, teste-as com cenários específicos. Invente
cenários que explorem casos extremos e forcem o usuário a ser preciso sobre os limites entre os
conceitos.

### Cruze com o código

Quando o usuário afirmar como algo funciona, verifique se o código concorda. Se encontrar uma
contradição, traga à tona: "Seu código cancela Pedidos inteiros, mas você acabou de dizer que
cancelamento parcial é possível — qual está certo?"

### Atualize o CONTEXT.md de forma inline

Quando um termo for resolvido, atualize o `CONTEXT.md` na hora. Não acumule essas atualizações —
registre-as conforme acontecem. Use o formato em [CONTEXT-FORMAT.md](CONTEXT-FORMAT.md).
O `CONTEXT.md` deve ser totalmente desprovido de detalhes de implementação. Não trate o `CONTEXT.md`
como uma especificação, um rascunho ou um repositório para decisões de implementação. Ele é um
glossário e nada mais.

### Ofereça ADRs com parcimônia

Só ofereça criar um ADR quando os três critérios forem verdadeiros:

1. **Difícil de reverter** — o custo de mudar de ideia mais tarde é significativo
2. **Surpreendente sem contexto** — um leitor futuro vai se perguntar "por que fizeram dessa forma?"
3. **Resultado de uma troca real** — havia alternativas genuínas e você escolheu uma por razões
   específicas
   Se qualquer um dos três estiver ausente, pule o ADR. Use o formato
   em [ADR-FORMAT.md](ADR-FORMAT.md).

</supporting-info>
