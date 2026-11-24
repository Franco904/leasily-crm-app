#!/usr/bin/env bash
# Formata o diff da branch (ou os arquivos dados): quebra de linha pela IDE e ktlint, em dois
# ciclos IDE -> ktlint, e verifica a convergência no fim.
# FORMAT_IDE=off pula a etapa da IDE de propósito (CI): só ktlint (higiene, indentação e espaçamento).
# Sem a variável e sem Android Studio, avisa e segue só com o ktlint.
# Códigos de saída: 0 ok; 3 ok, mas sem Android Studio (só o ktlint foi aplicado); outro, falha.
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
    if [ "${FORMAT_IDE:-on}" != "off" ]; then
        scripts/format/format.sh ${files[@]+"${files[@]}"}
        local status=$?
        if [ "$status" -eq 3 ]; then
            without_studio=true
        elif [ "$status" -ne 0 ]; then
            return "$status"
        fi
    fi
    ./gradlew spotlessApply -q ${spotless_args[@]+"${spotless_args[@]}"}
}

# Dois ciclos IDE -> ktlint para o par convergir; sem a IDE um ciclo basta
cycle || exit $?
if [ "${FORMAT_IDE:-on}" != "off" ]; then
    cycle || exit $?
fi

if ! ./gradlew spotlessCheck -q ${spotless_args[@]+"${spotless_args[@]}"}; then
    echo "format-pipeline.sh: o formatador não converge (spotlessCheck falha após o último ciclo)" >&2
    exit 1
fi

if [ "$without_studio" = true ]; then
    echo "format-pipeline.sh: Android Studio não encontrado (STUDIO_BIN); só o ktlint foi aplicado" >&2
    exit 3
fi
