package com.fstengineering.leasilycrm.core.domain.utils

/**
 * Resultado de uma operação: [Success] com o dado ou [Error] com um [BaseError]. O erro é sempre
 * [BaseError]; quem precisa distinguir o tipo (`ApplicationError`, `DomainError`,
 * `ValidationErrors`) ramifica com `when`/`is` sobre o valor.
 */
sealed interface AppResult<out D> {
    data class Success<out D>(val data: D) : AppResult<D>
    data class Error(val error: BaseError) : AppResult<Nothing>
}
