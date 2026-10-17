---
name: to-pr
description: Abre um PR no GitHub com corpo seguindo `.github/pull_request_template.md`, com ênfase em plano de QA ponta-a-ponta derivado do tracer bullet da issue. Use quando o usuário quiser abrir um PR, gerar o corpo de um PR ou criar o plano de QA de uma entrega.
argument-hint: "Qual o número da issue e a branch de destino? (ex: 42 main)"
---

## Invocação

`/to-pr <issue-number> [target-branch]`

- `<issue-number>`: número da issue GitHub. Se omitido, inferir do nome da branch (ex:
  `issue-1422` → #1422). Se não for possível inferir, perguntar ao usuário.
- `[target-branch]`: branch de destino do PR. Default: `main`.

---

## Concisão (obrigatório)

O PR é lido por revisor e QA com pouco tempo. Seja **extremamente conciso** em todas as seções
(Descrição, Análise de impactos, Orientações para revisão, Plano de QA); priorize concisão em detrimento de gramática.

- **Descrição:** causa (se bug) em 1–3 frases + correção em até 5 bullets. Sem listar exaustivamente
  cada método/arquivo modificado ou removido.
- **Orientações para revisão:** só o que o revisor não inferiria do diff (arquivo-chave, mudança de comportamento).
  Tipicamente 1–5 bullets.
- **Plano de QA:** um bullet de uma linha por cenário, agrupando cenários parecidos (ex.: "sem rede / sessão
  expirada"). Tipicamente 3–6 bullets. Pré-condição comum vai numa linha antes da lista, não
  repetida em cada cenário.
- Não justifique ausência de testes, nem detalhe nomes internos, nem repita o que já está no diff.

---

## Processo

### 1. Coletar contexto

```bash
git branch --show-current                        # nome da branch atual
git log <target-branch>...HEAD --oneline         # commits da branch
git diff <target-branch>...HEAD --stat           # arquivos alterados
```

Buscar a issue no GitHub com `gh issue view <issue-number>`. Extrair:

- Descrição e história de usuário
- Critérios de Aceitação
- Seção "Decisões de Implementação" (tracer bullet)
- Seção "Decisões de Teste"

### 2. Seção 1 — Análise de Impactos

Verificar se o diff toca arquivos em:

- `core/`: núcleo compartilhado entre módulos (value objects, erros, use cases e composables
  transversais)
- `core/infra/dataSources/database/` e `app/schemas/` — schema Room, DAOs e migrações
- `core/infra/backgroundWork/` e `core/infra/gateways/` — sincronização com o back-end
- `core/di/` e `*/di/` — módulos de injeção de dependências
- `core/presentation/theme/` e `core/presentation/composables/` — tema e componentes compartilhados
  entre telas
- `MainActivity.kt` e grafo de navegação — rotas entre telas
- `app/src/main/AndroidManifest.xml` — permissões, serviços e receivers

**Se sim:** preencher cada item do checklist do template com o que foi identificado.
**Se não:** preencher a seção com `–`.

### 3. Seção 2 — Orientações para Review

- Listar decisões de implementação não-óbvias (das "Decisões de Implementação" da issue)
- Sinalizar ADRs em `docs/adr/` seguidos ou criados nesta entrega
- Sinalizar desvios em relação aos CAs originais (se houver)
- Apontar arquivos-chave que merecem atenção extra do revisor

Só isso. Se não houver nada a destacar, preencher a seção com `–`.

### 4. Seção 3 — Plano de QA

Derivar o fluxo ponta-a-ponta a partir do tracer bullet da issue. Gerar como lista
de bullet points (não usar tabela), um item por cenário, no formato:

- **Cenário**: pré-condição → ação → resultado esperado

Use as categorias abaixo como checklist — inclua só as que o diff realmente afeta; omita (sem
mencionar) as que não se aplicam:

| Categoria             | O que testar                                                      |
|-----------------------|-------------------------------------------------------------------|
| Caminho feliz         | Fluxo principal completo conforme tracer bullet                   |
| Estado vazio          | Sem dados pré-existentes ou lista vazia                           |
| Erro de sincronização | Falha na comunicação com o back-end (timeout, sem rede)           |
| Dados inválidos       | Formulários com campos ausentes, formatos incorretos, limites     |
| Navegação             | Botão voltar, interrupção e retomada do fluxo                     |
| Sessão expirada       | Usuário deslogado, sessão ativa em outro dispositivo ou sem Creci |
| Edge cases de dados   | Campos opcionais ausentes, valores limítrofes, strings longas     |
| Impactos laterais     | Outros fluxos afetados pelas mudanças da Seção 1 (se aplicável)   |

### 5. Abrir PR

Montar o corpo com a estrutura exata do template `.github/pull_request_template.md`. Cabeçalho: use
"closes" para features; "fixes" para bugs. Substituir `#xyz` pelo número real da issue.

Título: #<issue-number> – <issue-title>

Publicar a branch e abrir o PR passando o corpo via heredoc, sem arquivo intermediário.

Concluído quando o PR existe no GitHub; apresentar a URL ao usuário.
