package com.fstengineering.leasilycrm.core.domain.utils

/**
 * Falha do Credential Manager. Fica em `domain/utils` porque `BaseError` é `sealed`: os subtipos
 * precisam estar no mesmo pacote e módulo.
 */
enum class CredentialsError : BaseError {
    UserCancellation,
    NoCredentialsFound,
    NoInternet,
    UnknownError,
}
