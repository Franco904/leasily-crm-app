#!/usr/bin/env bash
# Imprime o ref-base do ratchet do Spotless e do format.sh: o ponto onde a branch atual nasceu.
# Sem rede. Ordem de precedência (a primeira que resolver vence):
#   1. override explícito: $RATCHET_FROM (o Gradle lê -PratchetFrom antes de chamar este script;
#      `make format RATCHET_FROM=...` chega aqui pelo ambiente)
#   2. CI: merge-base com origin/$GITHUB_BASE_REF
#   3. base do PR gravada pelo gh em `branch.<nome>.gh-merge-base`, se presente
#   4. ancestral mais próximo: entre as demais branches (locais e origin/*), a de merge-base mais recente
#   5. origin/main
# Na main o ref-base é sempre origin/main. Sem nenhum ref resolvível, falha com mensagem.
set -euo pipefail

root="$(git rev-parse --show-toplevel)"
cd "$root"

if [ -n "${RATCHET_FROM:-}" ]; then
    echo "$RATCHET_FROM"
    exit 0
fi

merge_base_with() {
    git rev-parse --verify --quiet "$1^{commit}" > /dev/null || return 1
    git merge-base HEAD "$1"
}

if [ -n "${GITHUB_BASE_REF:-}" ] && merge_base_with "origin/$GITHUB_BASE_REF"; then
    exit 0
fi

branch="$(git rev-parse --abbrev-ref HEAD)"

if [ "$branch" = "main" ] && merge_base_with origin/main; then
    exit 0
fi

pr_base="$(git config --get "branch.$branch.gh-merge-base" || true)"
if [ -n "$pr_base" ]; then
    for ref in "origin/$pr_base" "$pr_base"; do
        if merge_base_with "$ref"; then
            exit 0
        fi
    done
fi

# Ancestral mais próximo: menor distância entre HEAD e o merge-base de cada outra branch
best_ref=""
best_distance=""
while IFS= read -r ref; do
    [ "$ref" = "$branch" ] && continue
    [ "$ref" = "origin/$branch" ] && continue
    [ "$ref" = "origin/HEAD" ] && continue
    base="$(merge_base_with "$ref" 2> /dev/null)" || continue
    # Branch empilhada sobre esta (contém HEAD e avançou além dele) não é ancestral
    if git merge-base --is-ancestor HEAD "$ref" && [ "$(git rev-parse "$ref")" != "$(git rev-parse HEAD)" ]; then
        continue
    fi
    distance="$(git rev-list --count "$base..HEAD")"
    if [ -z "$best_distance" ] || [ "$distance" -lt "$best_distance" ]; then
        best_distance="$distance"
        best_ref="$base"
    fi
done < <(git for-each-ref --format='%(refname:short)' refs/heads refs/remotes/origin)

if [ -n "$best_ref" ]; then
    echo "$best_ref"
    exit 0
fi

if ! merge_base_with origin/main; then
    echo "ratchet-base.sh: nenhum ref-base resolvido (sem origin/main); use RATCHET_FROM ou fetch-depth: 0" >&2
    exit 1
fi
