#!/usr/bin/env bash
# Formata o diff da branch (ou os arquivos dados): layout pela IDE e higiene pelo ktlint, em dois
# ciclos IDE -> ktlint, e verifica a convergência no fim.
# Códigos de saída: 0 ok; 3 ok, mas sem Android Studio (só a higiene foi aplicada); outro, falha.
set -uo pipefail

root="$(git rev-parse --show-toplevel)"
cd "$root"

files=("$@")
spotless_args=()
if [ "${#files[@]}" -gt 0 ]; then
    patterns="$(for file in "${files[@]}"; do
        printf '.*/%s,' "$(printf '%s' "$file" | sed -e 's/[][\.^$*+?(){}|]/\\&/g')"
    done)"
    spotless_args=("-PspotlessFiles=${patterns%,}")
fi

without_studio=false

cycle() {
    scripts/format/format.sh ${files[@]+"${files[@]}"}
    local status=$?
    if [ "$status" -eq 3 ]; then
        without_studio=true
    elif [ "$status" -ne 0 ]; then
        return "$status"
    fi
    ./gradlew spotlessApply -q ${spotless_args[@]+"${spotless_args[@]}"}
}

cycle || exit $?
cycle || exit $?

if ! ./gradlew spotlessCheck -q ${spotless_args[@]+"${spotless_args[@]}"}; then
    echo "format-pipeline.sh: o formatador não converge (spotlessCheck falha após dois ciclos)" >&2
    exit 1
fi

if [ "$without_studio" = true ]; then
    echo "format-pipeline.sh: Android Studio não encontrado (STUDIO_BIN); só a higiene foi aplicada" >&2
    exit 3
fi
