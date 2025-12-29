package com.fstengineering.leasilycrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.fstengineering.leasilycrm.core.application.backgroundWork.enqueueLazyWritesWorker
import com.fstengineering.leasilycrm.core.application.backgroundWork.enqueueReadsWorker
import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.LazyWritesWorker
import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.ReadsWorker
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.domain.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.domain.services.interfaces.UserSessionService.UserSessionEvent
import com.fstengineering.leasilycrm.core.presentation.composables.AppErrorBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.AppSnackbarHost
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val userSessionService: UserSessionService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun setContent() {
        val startedFromStayNotificationPopUp = intent
            ?.getBooleanExtra(NAVIGATE_TO_STAYS_EXTRA, false) == true

        setContent {
            AppTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()

                var errorBottomSheetData: ErrorBottomSheetData? by remember { mutableStateOf(null) }
                val sheetState = rememberModalBottomSheetState()
                val snackBarHostState = remember { SnackbarHostState() }

                fun onUiEvent(uiEvent: UiEvent) {
                    when (uiEvent) {
                        is UiEvent.ShowErrorBottomSheet -> {
                            errorBottomSheetData = uiEvent.error
                        }

                        is UiEvent.ShowSnackBar -> {
                            coroutineScope.launch {
                                val message = context.getString(uiEvent.message)
                                snackBarHostState.showSnackbar(message)
                            }
                        }

                        is UiEvent.ShowActionSnackBar -> {
                            coroutineScope.launch {
                                val message = context.getString(uiEvent.message)
                                val actionLabel = context.getString(uiEvent.actionLabel)

                                val result = snackBarHostState.showSnackbar(
                                    message = message,
                                    actionLabel = actionLabel,
                                    duration = SnackbarDuration.Long,
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    uiEvent.action()
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    userSessionService.userSessionEvents.collect { event ->
                        val isSessionExpired = event is UserSessionEvent.SessionExpiredEvent

                        cancelAppWorkers()
                        navController.navigate(Destinations.SignIn(isSessionExpired))
                    }
                }

                Scaffold(
                    snackbarHost = { AppSnackbarHost(snackbarHostState = snackBarHostState) },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Destinations.Root,
                        builder = {
                            destinations(
                                navController = navController,
                                startedFromStayNotificationPopUp = startedFromStayNotificationPopUp,
                                onSignIn = { enqueueAppWorkers() },
                                onUiEvent = { uiEvent -> onUiEvent(uiEvent) }
                            )
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )

                    if (errorBottomSheetData != null) {
                        AppErrorBottomSheet(
                            sheetState = sheetState,
                            errorBottomSheetData = errorBottomSheetData!!,
                            onDismissRequest = { errorBottomSheetData = null },
                            onUiEvent = { uiEvent -> onUiEvent(uiEvent) }
                        )
                    }
                }
            }
        }
    }

    private fun enqueueAppWorkers() {
        val workManager = WorkManager.getInstance(this@MainActivity)

        workManager.enqueueLazyWritesWorker()
        workManager.enqueueReadsWorker()
    }

    private fun cancelAppWorkers() {
        WorkManager.getInstance(this@MainActivity).run {
            cancelUniqueWork(LazyWritesWorker.UNIQUE_NAME)
            cancelUniqueWork(ReadsWorker.UNIQUE_NAME)
        }
    }

    companion object {
        const val NAVIGATE_TO_STAYS_EXTRA = "navigate_to_stays"
    }
}
