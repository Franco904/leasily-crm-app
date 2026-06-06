---
name: tdd
description: Desenvolvimento orientado a testes com o ciclo red-green-refactor. Use quando o usuário quiser construir funcionalidades ou corrigir bugs usando TDD, mencionar "red-green-refactor", quiser testes de integração ou pedir desenvolvimento test-first.
---

# Desenvolvimento Orientado a Testes (TDD)

## Filosofia

**Princípio central**: Testes devem verificar comportamento através de interfaces públicas, não detalhes de implementação. O código pode mudar completamente; os testes não deveriam.

**Bons testes** são no estilo de integração: exercitam caminhos reais de código através de APIs públicas. Descrevem _o que_ o sistema faz, não _como_ faz. Um bom teste lê como uma especificação — "usuário pode finalizar compra com carrinho válido" diz exatamente qual capacidade existe. Esses testes sobrevivem a refatorações porque não se importam com a estrutura interna.

**Maus testes** estão acoplados à implementação. Eles mockam colaboradores internos, testam métodos privados ou verificam por meios externos (como consultar o banco diretamente em vez de usar a interface). O sinal de alerta: o teste quebra quando você refatora, mas o comportamento não mudou. Se você renomear uma função interna e os testes falharem, esses testes estavam testando implementação, não comportamento.

Veja [tests.md](tests.md) para exemplos e [mocking.md](mocking.md) para diretrizes de mocking.

## Anti-Padrão: Fatias Horizontais

**NÃO escreva todos os testes primeiro e depois toda a implementação.** Isso é "fatiamento horizontal" — tratar o RED como "escrever todos os testes" e o GREEN como "escrever todo o código."

Isso produz **testes ruins**:

- Testes escritos em massa testam comportamento _imaginado_, não _real_
- Você acaba testando a _forma_ das coisas (estruturas de dados, assinaturas de funções) em vez do comportamento voltado ao usuário
- Os testes ficam insensíveis a mudanças reais — passam quando o comportamento quebra, falham quando está tudo bem
- Você vai além do que enxerga, comprometendo-se com estrutura de testes antes de entender a implementação

**Abordagem correta**: Fatias verticais via tracer bullets. Um teste → uma implementação → repita. Cada teste responde ao que você aprendeu do ciclo anterior. Como você acabou de escrever o código, sabe exatamente qual comportamento importa e como verificá-lo.

```
ERRADO (horizontal):
  RED:   test1, test2, test3, test4, test5
  GREEN: impl1, impl2, impl3, impl4, impl5

CERTO (vertical):
  RED→GREEN: test1→impl1
  RED→GREEN: test2→impl2
  RED→GREEN: test3→impl3
  ...
```

## Fluxo de Trabalho

> **Se o Planejamento já estiver concluído**, com interface e comportamentos bem definidos (ex.: após uma sessão de planejamento), **pule direto para o Passo 2**.

### 1. Planejamento

Ao explorar a base de código, use o glossário de domínio do projeto para que os nomes dos testes e o vocabulário da interface correspondam à linguagem do projeto, e respeite os ADRs na área que você está tocando.

Antes de escrever qualquer código:

- [ ] Confirmar com o usuário quais mudanças de interface são necessárias
- [ ] Confirmar com o usuário quais comportamentos testar (priorizar)
- [ ] Identificar oportunidades para [módulos profundos](deep-modules.md) (interface pequena, implementação profunda)
- [ ] Projetar interfaces para [testabilidade](interface-design.md)
- [ ] Listar os comportamentos a testar (não etapas de implementação)
- [ ] Obter aprovação do usuário sobre o plano

Pergunte: "Como deve ser a interface pública? Quais comportamentos são mais importantes de testar?"

**Você não pode testar tudo.** Confirme com o usuário exatamente quais comportamentos são mais importantes. Concentre o esforço de teste em caminhos críticos e lógica complexa, não em todos os casos extremos possíveis.

### 2. Tracer Bullet

Escreva UM teste que confirme UMA coisa sobre o sistema:

```
RED:   Escrever teste para o primeiro comportamento → teste falha
GREEN: Escrever código mínimo para passar → teste passa
```

Este é o seu tracer bullet — prova que o caminho funciona de ponta a ponta.

### 3. Loop Incremental

Para cada comportamento restante:

```
RED:   Escrever próximo teste → falha
GREEN: Código mínimo para passar → passa
```

Regras:

- Um teste por vez
- Apenas código suficiente para passar o teste atual
- Não antecipe testes futuros
- Mantenha os testes focados em comportamento observável

### 4. Refatorar

Após todos os testes passarem, procure [candidatos à refatoração](refactoring.md):

- [ ] Extrair duplicação
- [ ] Aprofundar módulos (mover complexidade para trás de interfaces simples)
- [ ] Aplicar princípios SOLID onde fizer sentido
- [ ] Considerar o que o novo código revela sobre o código existente
- [ ] Executar testes após cada etapa de refatoração

**Nunca refatore enquanto estiver no RED.** Chegue ao GREEN primeiro.

## Checklist Por Ciclo

```
[ ] Teste descreve comportamento, não implementação
[ ] Teste usa apenas interface pública
[ ] Teste sobreviveria a uma refatoração interna
[ ] Código é mínimo para este teste
[ ] Nenhuma funcionalidade especulativa adicionada
```
