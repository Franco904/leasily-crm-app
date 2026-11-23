# Leasily CRM — Guia do Agente de IA

Ver @README.md para visão geral do app, pré-requisitos, stack, estrutura do projeto, linguagem
ubíqua, convenções de nomenclatura, arquitetura, e comandos essenciais.
Este arquivo cobre apenas o que é específico de como um agente de IA deve trabalhar neste
repositório.

Repositório do backend: `leasily-crm-server/`

---

## Janela de Contexto

Ao chegar a ~40% da janela de contexto (margem de ±10%, ou seja, de 30% a 50%), conclua a demanda em
andamento se ela couber na margem e sugira `/fst:handoff` em uma linha. O handoff substitui a
compactação: a conversa continua em outra sessão, a partir do documento gerado.

---

## Regras determinísticas

Regra determinística (Konsist, formatador, CI) vale para o projeto todo e é verificável por um
agente de IA sozinho: regra que ele não consegue verificar é regra que ele pode pular. Ao propor,
escrever ou revisar uma regra, decida o que ela proíbe pelo **critério mecânico**.

- **Critério mecânico:** o que separa o uso bom do ruim sem julgamento — nome, localização, tipo de
  declaração, produtor × consumidor, arrange × assert, leitura pontual × coletor lançado. Quando
  existe, codifique o critério e mantenha a API.
- **Banimento:** quando a fronteira entre o uso bom e o ruim exige julgamento, bana a API e proponha
  a alternativa objetiva.
- **Sem exceção:** a regra vale para o projeto todo, sem allowlist, `@Suppress` nem `@Disabled`. A
  regra que precisar de exceção deve ser redesenhada.
- **Legado primeiro:** o código que viola deve ser migrado antes da regra ligar; portanto, toda
  regra entra com 0 violações.

```kotlin
// BOM: critério mecânico (leitura pontual × coletor lançado), então a API fica
val imovel = sut.watchImovel(imovelId).first()

// RUIM: sem critério mecânico, a API é banida. O coletor lançado foge do controle do teste
launch { sut.watchImovel(imovelId).collect { received += it } }
```

Ao desenhar uma regra, pergunte:

- Existe um critério mecânico que um teste automatizado consegue verificar?
- Se não, qual API banir, e qual é a alternativa que o agente usa no lugar?

---

## Commits e PRs

- **Idioma:** sempre em português.
- **Coautoria proibida:** nunca incluir `Co-Authored-By` (nem qualquer atribuição de autoria a IA)
  nas mensagens de commit.
- **Tipos de commit:** prefixe a mensagem com o tipo, ex: `fix: sobreposição de períodos na
  validação de overbooking`.
    - `feat`: nova funcionalidade ou melhoria em existente
    - `fix`: correção de bug
    - `refactor`: mesma funcionalidade, apenas melhoria de código/manutenção
    - `build`: afeta arquivos de build
    - `chore`: afeta arquivos auxiliares secundários (ex: lint, scripts)
    - `test`: testes automatizados de qualquer tipo/nível
    - `doc`: documentação markdown/html
- **Descrição:** extremamente concisa. Priorize concisão em detrimento de gramática.
- **Atomicidade:** cada commit deve conter uma única mudança logicamente coesa.
- **Formatador e testes automatizados:** rode o formatador, linter e os testes automatizados do
  projeto (`make format && make test`) antes de cada commit.

---

## Referências ubíquas

Antes de agir, **sempre** consulte as referências ubíquas do projeto e balize suas decisões por
elas. Diretrizes
de camada e de teste ficam em `docs/guidelines/` — consulte a da camada/nível que a tarefa toca.

| Arquivo                             | Conteúdo                                                                                                                                                   |
|-------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @GLOSSARY.md                        | Glossário da linguagem ubíqua e mapa dos contextos delimitados — base para nomes de classes, métodos, variáveis e testes                                   |
| @docs/guidelines/0001-subdomains.md | Classificação estratégica dos subdomínios e orientação para padrões táticos de DDD                                                                         |
| `docs/adr/`                         | ADRs com decisões de arquitetura do sistema; não questione, apenas siga                                                                                    |
| `docs/guidelines/`                  | Testes (0002–0005), camadas presentation/application/domain/infra (0006–0009), design de interface (0010), erros (0011), comentários (0012) e nomes (0013) |
