# Leasily CRM — Guia do Agente de IA

Ver @README.md para visão geral do app, pré-requisitos, stack, estrutura do projeto, linguagem
ubíqua, convenções de nomenclatura, arquitetura, e comandos essenciais.
Este arquivo cobre apenas o que é específico de como um agente de IA deve trabalhar neste
repositório.

Repositório do backend: `leasily-crm-server/`

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
