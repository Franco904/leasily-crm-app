package com.fstengineering.leasilycrm

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    data object Root

    @Serializable
    data class SignIn(val hasSessionExpired: Boolean)

    @Serializable
    data class Home(val startedFromStayNotificationPopUp: Boolean) {
        @Serializable
        data object Dashboard

        @Serializable
        data object Leads

        @Serializable
        data object Tenants

        @Serializable
        data object OccupiedPeriods
    }

    @Serializable
    data object Settings

    @Serializable
    data object Profile

    @Serializable
    data object Imoveis

    @Serializable
    data class ImovelRegistration(val imovelId: Long?) {
        @Serializable
        data object ImovelRegistrationStep1

        @Serializable
        data object ImovelRegistrationStep2

        @Serializable
        data object ImovelRegistrationStep3

        @Serializable
        data object ImovelRegistrationStep4
    }

    @Serializable
    data class ImovelDashboard(val imovelId: Long)

    @Serializable
    data class ImovelDetails(val imovelId: Long)

    @Serializable
    data class ImovelOcupacao(val imovelId: Long)

    @Serializable
    data object Proprietarios

    @Serializable
    data class ProprietarioRegistration(val proprietarioId: Long?)

    @Serializable
    data class ProprietarioDetails(val proprietarioId: Long)

    @Serializable
    data class ClienteRegistration(val clienteId: Long?)

    @Serializable
    data class ClienteDetails(val clienteId: Long)

    @Serializable
    data class AtendimentoRegistration(
        val atendimentoId: Long?,
        val clienteId: Long,
    ) {
        @Serializable
        data object AtendimentoRegistrationStep1

        @Serializable
        data object AtendimentoRegistrationStep2

        @Serializable
        data object AtendimentoRegistrationStep3
    }

    @Serializable
    data class OccupiedPeriodRegistration(
        val occupiedPeriodId: Long?,
        val imovelId: Long?,
        val clienteId: Long?,
        val isUnavailablePeriod: Boolean,
    )

    @Serializable
    data class OccupiedPeriodDetails(
        val occupiedPeriodId: Long,
        val cameFromImovel: Boolean,
        val cameFromTenant: Boolean,
    )
}
