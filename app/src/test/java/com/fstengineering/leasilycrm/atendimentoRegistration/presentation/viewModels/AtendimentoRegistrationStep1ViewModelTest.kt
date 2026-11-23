package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels

import app.cash.turbine.test
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoStep1ErrorUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.fields.AtendimentoStep1Field
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep1ViewModel.UiEvent
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimal
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class AtendimentoRegistrationStep1ViewModelTest {
    private lateinit var sut: AtendimentoRegistrationStep1ViewModel

    @BeforeEach
    fun setUp() {
        sut = AtendimentoRegistrationStep1ViewModel()
    }

    @Nested
    @DisplayName("validateAtendimentoStep1")
    inner class ValidateAtendimentoStep1Tests {
        @Test
        fun `Deve avançar para a próxima etapa quando validada, dado que nenhuma proximidade do mar foi escolhida`() =
            runTest {
                val form = createForm(seaProximityTypeId = null)

                sut.uiEvents.test {
                    sut.validateAtendimentoStep1(form)
                    advanceTimeBy(200)

                    awaitItem() shouldBeEqualTo UiEvent.ValidateSuccess
                }
            }

        @Test
        fun `Deve avançar para a próxima etapa quando validada, dado que a proximidade do mar não exige distância`() =
            runTest {
                val form = createForm(seaProximityTypeId = ImovelSeaProximityType.SEA_VIEW_ID)

                sut.uiEvents.test {
                    sut.validateAtendimentoStep1(form)
                    advanceTimeBy(200)

                    awaitItem() shouldBeEqualTo UiEvent.ValidateSuccess
                }
            }

        @Test
        fun `Deve avançar para a próxima etapa quando validada, dado que a distância ao mar foi informada`() =
            runTest {
                val form = createForm(
                    seaProximityTypeId = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                    distanceToSea = 300.toDecimal(),
                )

                sut.uiEvents.test {
                    sut.validateAtendimentoStep1(form)
                    advanceTimeBy(200)

                    awaitItem() shouldBeEqualTo UiEvent.ValidateSuccess
                }
            }

        @Test
        fun `Deve rolar até a distância ao mar sem avançar quando validada, dado que a distância não foi informada`() =
            runTest {
                val form = createForm(
                    seaProximityTypeId = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                    distanceToSea = null,
                )

                sut.uiEvents.test {
                    sut.validateAtendimentoStep1(form)
                    advanceTimeBy(200)

                    awaitItem() shouldBeEqualTo UiEvent.ScrollToField(AtendimentoStep1Field.DistanceToSea)
                }
            }

        @Test
        fun `Deve exibir erro na distância ao mar quando validada, dado que a distância não foi informada`() =
            runTest {
                val form = createForm(
                    seaProximityTypeId = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                    distanceToSea = null,
                )

                sut.formErrors.test {
                    awaitItem() shouldBeEqualTo AtendimentoStep1ErrorUiState()

                    sut.validateAtendimentoStep1(form)
                    advanceTimeBy(200)

                    awaitItem().errors[AtendimentoStep1Field.DistanceToSea].shouldNotBeNull()
                }
            }

        @Test
        fun `Deve limpar o erro anterior quando validada de novo, dado que a distância passou a ser informada`() =
            runTest {
                val invalidForm = createForm(
                    seaProximityTypeId = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                    distanceToSea = null,
                )
                sut.validateAtendimentoStep1(invalidForm)
                advanceTimeBy(200)

                sut.formErrors.test {
                    awaitItem().errors[AtendimentoStep1Field.DistanceToSea].shouldNotBeNull()

                    sut.validateAtendimentoStep1(invalidForm.copy(preferredDistanceToSea = 300.toDecimal()))
                    advanceTimeBy(200)

                    awaitItem().errors[AtendimentoStep1Field.DistanceToSea].shouldBeNull()
                }
            }
    }

    @Nested
    @DisplayName("onAtendimentoFormChange")
    inner class OnAtendimentoFormChangeTests {
        @Test
        fun `Deve limpar o erro da distância ao mar quando a distância é alterada`() = runTest {
            val oldForm = createForm(
                seaProximityTypeId = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                distanceToSea = null,
            )
            sut.validateAtendimentoStep1(oldForm)
            advanceTimeBy(200)

            sut.formErrors.test {
                awaitItem().errors[AtendimentoStep1Field.DistanceToSea].shouldNotBeNull()

                sut.onAtendimentoFormChange(
                    newForm = oldForm.copy(preferredDistanceToSea = 300.toDecimal()),
                    oldForm = oldForm,
                )

                awaitItem().errors[AtendimentoStep1Field.DistanceToSea].shouldBeNull()
            }
        }

        @Test
        fun `Deve manter o erro da distância ao mar quando outro campo é alterado`() = runTest {
            val oldForm = createForm(
                seaProximityTypeId = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                distanceToSea = null,
            )
            sut.validateAtendimentoStep1(oldForm)
            advanceTimeBy(200)

            sut.formErrors.test {
                val errorState = awaitItem()
                errorState.errors[AtendimentoStep1Field.DistanceToSea].shouldNotBeNull()

                sut.onAtendimentoFormChange(
                    newForm = oldForm.copy(observations = faker.random.nextUUID()),
                    oldForm = oldForm,
                )

                expectNoEvents()
            }
        }
    }

    @Nested
    @DisplayName("savePreferredNeighborhood")
    inner class SavePreferredNeighborhoodTests {
        @Test
        fun `Deve exibir erro de obrigatório sem confirmar quando o bairro é salvo, dado que está em branco`() =
            runTest {
                sut.onNeighborhoodChange("   ")

                sut.neighborhoodFormError.test {
                    awaitItem().shouldBeNull()

                    sut.savePreferredNeighborhood()

                    awaitItem().shouldNotBeNull()
                }
                sut.uiEvents.test { expectNoEvents() }
            }

        @Test
        fun `Deve confirmar o bairro quando salvo, dado que foi informado`() = runTest {
            sut.onNeighborhoodChange("Campeche")

            sut.uiEvents.test {
                sut.savePreferredNeighborhood()

                awaitItem() shouldBeEqualTo UiEvent.FinishSearchedNeighborhoodSave
            }
        }

        @Test
        fun `Deve limpar o erro do bairro quando salvo com bairro informado, dado que havia erro anterior`() =
            runTest {
                sut.onNeighborhoodChange(null)
                sut.savePreferredNeighborhood()

                sut.neighborhoodFormError.test {
                    awaitItem().shouldNotBeNull()

                    sut.onNeighborhoodChange("Campeche")
                    sut.savePreferredNeighborhood()

                    awaitItem().shouldBeNull()
                }
            }
    }

    @Nested
    @DisplayName("clearPreferredNeighborhoodErrors")
    inner class ClearPreferredNeighborhoodErrorsTests {
        @Test
        fun `Deve limpar o erro do bairro quando solicitado`() = runTest {
            sut.onNeighborhoodChange(null)
            sut.savePreferredNeighborhood()

            sut.neighborhoodFormError.test {
                awaitItem().shouldNotBeNull()

                sut.clearPreferredNeighborhoodErrors()

                awaitItem().shouldBeNull()
            }
        }
    }

    private fun createForm(
        seaProximityTypeId: Long?,
        distanceToSea: AppDecimal? = null,
    ) = AtendimentoDraftFormUiState.Loaded.empty(clienteId = 1L, clienteName = faker.random.nextUUID())
        .copy(
            preferredSeaProximityTypeId = seaProximityTypeId,
            preferredDistanceToSea = distanceToSea,
        )
}
