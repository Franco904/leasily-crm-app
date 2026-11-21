package com.fstengineering.leasilycrm.core.domain.utils

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Erros de domínio carregados por um erro devolvido por uma factory de domínio: o próprio
 * [DomainError] ou a lista de um [ValidationErrors]. Uma factory só devolve esses dois; qualquer
 * outro erro é bug e derruba o app (`0011`, passo 4).
 */
fun BaseError.toDomainErrors(): ImmutableList<DomainError> = when (this) {
    is DomainError -> persistentListOf(this)
    is ValidationErrors -> errors
    is ApplicationError ->
        error("Factory de domínio devolveu um erro que não é de domínio: $this")
}
