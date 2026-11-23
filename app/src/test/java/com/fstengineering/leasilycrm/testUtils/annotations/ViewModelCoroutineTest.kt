package com.fstengineering.leasilycrm.testUtils.annotations

import com.fstengineering.leasilycrm.testUtils.extensions.MainDispatcherExtension
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Anotação obrigatória em testes de ViewModel (`docs/guidelines/0005-presentation-tests.md`):
 * `Dispatchers.Main` substituído por `TestDispatcher`.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(MainDispatcherExtension::class)
annotation class ViewModelCoroutineTest
