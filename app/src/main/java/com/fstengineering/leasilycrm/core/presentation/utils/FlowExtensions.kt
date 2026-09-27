package com.fstengineering.leasilycrm.core.presentation.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Coleta o [Flow] apenas enquanto o [lifecycle] estiver ao menos em [minActiveState], recriando a
 * coleta a cada retomada e cancelando-a a cada parada (via [repeatOnLifecycle]).
 *
 * Encapsula o par `repeatOnLifecycle { collect { } }` usado para consumir `Flow` de eventos one-shot
 * de ViewModel numa `@Composable` (ver `docs/guidelines/0007`), evitando que emissões sejam
 * entregues com a tela em segundo plano. Deve ser chamada de dentro de um `LaunchedEffect`.
 */
suspend fun <T> Flow<T>.collectWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit,
) {
    lifecycle.repeatOnLifecycle(minActiveState) {
        collect(action)
    }
}
