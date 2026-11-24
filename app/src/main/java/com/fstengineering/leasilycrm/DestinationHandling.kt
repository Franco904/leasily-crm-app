package com.fstengineering.leasilycrm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.AtendimentoRegistrationStep1Screen
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.AtendimentoRegistrationStep2Screen
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.AtendimentoRegistrationStep3Screen
import com.fstengineering.leasilycrm.clienteDetails.presentation.ClienteDetailsScreen
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationScreen
import com.fstengineering.leasilycrm.clientes.presentation.hospedes.HospedesPage
import com.fstengineering.leasilycrm.clientes.presentation.leads.LeadsPage
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.dashboard.presentation.DashboardPage
import com.fstengineering.leasilycrm.home.HomeScreen
import com.fstengineering.leasilycrm.imoveis.presentation.ImoveisScreen
import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardScreen
import com.fstengineering.leasilycrm.imovelDetails.presentation.ImovelDetailsScreen
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.ImovelOcupacaoScreen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep1Screen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep2Screen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep3Screen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep4Screen
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsScreen
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.OccupiedPeriodRegistrationScreen
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsPage
import com.fstengineering.leasilycrm.profile.presentation.ProfileScreen
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.ProprietarioDetailsScreen
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationScreen
import com.fstengineering.leasilycrm.proprietarios.presentation.ProprietariosScreen
import com.fstengineering.leasilycrm.root.RootScreen
import com.fstengineering.leasilycrm.settings.SettingsScreen
import com.fstengineering.leasilycrm.signin.presentation.SignInScreen
import org.koin.androidx.compose.koinViewModel

private fun newAtendimentoOf(clienteId: Long) = Destinations.AtendimentoRegistration(
    atendimentoId = null,
    clienteId = clienteId,
)

private fun newStayOf(clienteId: Long) = Destinations.OccupiedPeriodRegistration(
    occupiedPeriodId = null,
    imovelId = null,
    clienteId = clienteId,
    isUnavailablePeriod = false,
)

private fun viewStayOf(stayId: Long, cameFromCliente: Boolean) = Destinations.OccupiedPeriodDetails(
    occupiedPeriodId = stayId,
    cameFromImovel = false,
    cameFromTenant = cameFromCliente,
)

private const val STAY_REGISTRATION_SUCCESS_KEY = "stay_registration_success"

fun NavGraphBuilder.destinations(
    navController: NavController,
    startedFromStayNotificationPopUp: Boolean,
    onSignIn: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
) {
    composable<Destinations.Root> {
        RootScreen(
            viewModel = koinViewModel(),
            startedFromStayNotificationPopUp = startedFromStayNotificationPopUp,
            onNavigateToFirstDestination = { firstDestination ->
                navController.navigate(firstDestination) {
                    val startDestination =
                        navController.graph.startDestinationRoute ?: return@navigate

                    popUpTo(startDestination) { inclusive = true }
                }
            },
        )
    }

    composable<Destinations.SignIn> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.SignIn>()

        SignInScreen(
            hasSessionExpired = args.hasSessionExpired,
            viewModel = koinViewModel(),
            onSignIn = {
                onSignIn()

                val destination = Destinations.Home(startedFromStayNotificationPopUp = false)
                navController.navigate(destination) {
                    val startDestination =
                        navController.graph.startDestinationRoute ?: return@navigate

                    popUpTo(startDestination) { inclusive = true }
                }
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Home> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.Home>()
        val savedStateHandle = navController.currentBackStackEntry
            ?.savedStateHandle

        var isStayRegistrationSuccess by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            savedStateHandle
                ?.getLiveData<Boolean>(STAY_REGISTRATION_SUCCESS_KEY)
                ?.observeForever { success ->
                    isStayRegistrationSuccess = success
                    savedStateHandle.remove<String>(STAY_REGISTRATION_SUCCESS_KEY)
                }
        }

        HomeScreen(
            viewModel = koinViewModel(),
            startedFromStayCheckInCheckOutNotification = args.startedFromStayNotificationPopUp,
            isStayRegistrationSuccess = isStayRegistrationSuccess,
            onSettingsSelect = {
                navController.navigateAndPopAllTo(Destinations.Settings)
            },
            onProfileSelect = {
                navController.navigateAndPopAllTo(Destinations.Profile)
            },
            dashboardPage = {
                DashboardPage(
                    viewModel = koinViewModel(),
                    onImoveisSelect = { navController.navigate(Destinations.Imoveis) },
                    onProprietariosSelect = { navController.navigate(Destinations.Proprietarios) },
                )
            },
            leadsPage = { isSearching, searchInput ->
                LeadsPage(
                    viewModel = koinViewModel(),
                    isSearchingLead = isSearching,
                    searchInput = searchInput,
                    onRegisterLead = { clienteId ->
                        navController.navigate(Destinations.ClienteRegistration(clienteId))
                    },
                    onViewLeadDetails = { clienteId ->
                        navController.navigate(Destinations.ClienteDetails(clienteId))
                    },
                    onNewAtendimento = { clienteId ->
                        navController.navigate(
                            newAtendimentoOf(clienteId)
                        )
                    },
                    onNewStay = { clienteId -> navController.navigate(newStayOf(clienteId)) },
                    onViewStay = { stayId ->
                        navController.navigate(
                            viewStayOf(
                                stayId,
                                cameFromCliente = true
                            )
                        )
                    },
                    onUiEvent = onUiEvent,
                )
            },
            hospedesPage = { isSearching, searchInput ->
                HospedesPage(
                    viewModel = koinViewModel(),
                    isSearchingHospede = isSearching,
                    searchInput = searchInput,
                    onRegisterHospede = { clienteId ->
                        navController.navigate(Destinations.ClienteRegistration(clienteId))
                    },
                    onViewHospedeDetails = { clienteId ->
                        navController.navigate(Destinations.ClienteDetails(clienteId))
                    },
                    onNewAtendimento = { clienteId ->
                        navController.navigate(
                            newAtendimentoOf(clienteId)
                        )
                    },
                    onNewStay = { clienteId -> navController.navigate(newStayOf(clienteId)) },
                    onViewStay = { stayId ->
                        navController.navigate(
                            viewStayOf(
                                stayId,
                                cameFromCliente = true
                            )
                        )
                    },
                    onUiEvent = onUiEvent,
                )
            },
            occupiedPeriodsPage = {
                OccupiedPeriodsPage(
                    viewModel = koinViewModel(),
                    onRegisterOccupiedPeriod = { stayId, isUnavailablePeriod ->
                        val destination = Destinations.OccupiedPeriodRegistration(
                            occupiedPeriodId = stayId,
                            imovelId = null,
                            clienteId = null,
                            isUnavailablePeriod = isUnavailablePeriod,
                        )

                        navController.navigate(destination)
                    },
                    onViewOccupiedPeriodDetails = { stayId ->
                        navController.navigate(viewStayOf(stayId, cameFromCliente = false))
                    },
                    onUiEvent = onUiEvent,
                )
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Settings> {
        SettingsScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Profile> {
        ProfileScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Imoveis> {
        ImoveisScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onRegisterImovel = { imovelId ->
                navController.navigate(Destinations.ImovelRegistration(imovelId))
            },
            onViewImovelDashboard = { imovelId ->
                navController.navigate(Destinations.ImovelDashboard(imovelId))
            },
            onViewImovelDetails = { imovelId ->
                navController.navigate(Destinations.ImovelDetails(imovelId))
            },
            onNewStay = { imovelId, isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = imovelId,
                    clienteId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    navigation<Destinations.ImovelRegistration>(
        startDestination = Destinations.ImovelRegistration.ImovelRegistrationStep1,
    ) {
        composable<Destinations.ImovelRegistration.ImovelRegistrationStep1> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep1Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onRegisterNewProprietario = {
                    navController.navigate(Destinations.ProprietarioRegistration(null))
                },
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onNext = {
                    val nextRoute = Destinations.ImovelRegistration.ImovelRegistrationStep2
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.ImovelRegistration.ImovelRegistrationStep2> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep2Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute = Destinations.ImovelRegistration.ImovelRegistrationStep3
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.ImovelRegistration.ImovelRegistrationStep3> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep3Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute = Destinations.ImovelRegistration.ImovelRegistrationStep4
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.ImovelRegistration.ImovelRegistrationStep4> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep4Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onUiEvent = onUiEvent,
            )
        }
    }

    composable<Destinations.ImovelDashboard> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ImovelDashboard>()

        ImovelDashboardScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditImovel = {
                navController.navigate(Destinations.ImovelRegistration(args.imovelId))
            },
            onViewImovelDetails = {
                navController.navigate(Destinations.ImovelDetails(args.imovelId))
            },
            onViewProprietarioDetails = { proprietarioId ->
                navController.navigate(Destinations.ProprietarioDetails(proprietarioId))
            },
            onViewAllImovelStays = {
                navController.navigate(Destinations.ImovelOcupacao(args.imovelId))
            },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = args.imovelId,
                    clienteId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = true,
                    cameFromTenant = false,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ImovelDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ImovelDetails>()

        ImovelDetailsScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditImovel = {
                navController.navigate(
                    Destinations.ImovelRegistration(args.imovelId),
                )
            },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = args.imovelId,
                    clienteId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ImovelOcupacao> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ImovelOcupacao>()

        ImovelOcupacaoScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = args.imovelId,
                    clienteId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = true,
                    cameFromTenant = false,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Proprietarios> {
        ProprietariosScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onRegisterProprietario = { proprietarioId ->
                navController.navigate(Destinations.ProprietarioRegistration(proprietarioId))
            },
            onViewProprietarioDetails = { proprietarioId ->
                navController.navigate(Destinations.ProprietarioDetails(proprietarioId))
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ProprietarioRegistration> {
        ProprietarioRegistrationScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ProprietarioDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ProprietarioDetails>()

        ProprietarioDetailsScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditProprietario = {
                navController.navigate(Destinations.ProprietarioRegistration(args.proprietarioId))
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ClienteRegistration> {
        ClienteRegistrationScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ClienteDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ClienteDetails>()

        ClienteDetailsScreen(
            viewModel = koinViewModel(),
            onEditCliente = {
                navController.navigate(
                    Destinations.ClienteRegistration(clienteId = args.clienteId),
                )
            },
            onRegisterAtendimento = { atendimentoId ->
                val destination = Destinations.AtendimentoRegistration(
                    atendimentoId = atendimentoId,
                    clienteId = args.clienteId,
                )
                navController.navigate(destination)
            },
            onNewStay = {
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = null,
                    clienteId = args.clienteId,
                    isUnavailablePeriod = false,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = false,
                    cameFromTenant = true,
                )

                navController.navigate(destination)
            },
            onViewSentImovelDetails = { imovelId ->
                navController.navigate(Destinations.ImovelDashboard(imovelId))
            },
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    navigation<Destinations.AtendimentoRegistration>(
        startDestination = Destinations.AtendimentoRegistration.AtendimentoRegistrationStep1,
    ) {
        composable<Destinations.AtendimentoRegistration.AtendimentoRegistrationStep1> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.AtendimentoRegistration>()
            }

            AtendimentoRegistrationStep1Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.AtendimentoRegistration>(inclusive = true)
                },
                onNext = {
                    val nextRoute =
                        Destinations.AtendimentoRegistration.AtendimentoRegistrationStep2
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.AtendimentoRegistration.AtendimentoRegistrationStep2> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.AtendimentoRegistration>()
            }

            AtendimentoRegistrationStep2Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.AtendimentoRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute =
                        Destinations.AtendimentoRegistration.AtendimentoRegistrationStep3
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.AtendimentoRegistration.AtendimentoRegistrationStep3> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.AtendimentoRegistration>()
            }

            AtendimentoRegistrationStep3Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.AtendimentoRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onUiEvent = onUiEvent,
            )
        }
    }

    composable<Destinations.OccupiedPeriodRegistration> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.OccupiedPeriodRegistration>()
        val previousBackStackEntry = navController.previousBackStackEntry

        OccupiedPeriodRegistrationScreen(
            stayId = args.occupiedPeriodId,
            imovelId = args.imovelId,
            tenantId = args.clienteId,
            isUnavailablePeriod = args.isUnavailablePeriod,
            viewModel = koinViewModel(),
            onBack = { success ->
                previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(STAY_REGISTRATION_SUCCESS_KEY, success)

                navController.navigateUp()
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.OccupiedPeriodDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.OccupiedPeriodDetails>()

        OccupiedPeriodDetailsScreen(
            occupiedPeriodId = args.occupiedPeriodId,
            cameFromImovel = args.cameFromImovel,
            cameFromTenant = args.cameFromTenant,
            viewModel = koinViewModel(),
            onEdit = { stayId, imovelId, tenantId, isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = stayId,
                    imovelId = imovelId,
                    clienteId = tenantId,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewImovelDashboard = { imovelId ->
                navController.navigate(Destinations.ImovelDashboard(imovelId))
            },
            onViewTenantDetails = { tenantId ->
                navController.navigate(Destinations.ClienteDetails(tenantId))
            },
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }
}

private fun NavController.navigateAndPopAllTo(destination: Any) {
    navigate(destination) {
        val startDestination = graph.startDestinationRoute ?: return@navigate
        popUpTo(startDestination) { inclusive = true }

        launchSingleTop = true
    }
}
