package com.fstengineering.leasilycrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.fstengineering.leasilycrm.DestinationsHandler.destinations
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.presentation.composables.AppSnackbarHost
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent()
    }

    private fun setContent() {
        setContent {
            AppTheme {
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                Scaffold(
                    snackbarHost = {
                        AppSnackbarHost(
                            snackbarHostState = snackbarHostState,
                        )
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Destinations.Root,
                        builder = {
                            destinations(
                                navController = navController,
                                showSnackbar = { message ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message = message)
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
