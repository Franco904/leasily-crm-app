package com.fstengineering.leasilycrm.testUtils.annotations

import com.fstengineering.leasilycrm.testUtils.extensions.MainDispatcherExtension
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Anotação obrigatória em testes de ViewModel (`docs/guidelines/0005-presentation-tests.md`):
 * instância compartilhada pela classe e `Dispatchers.Main` substituído por `TestDispatcher`.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MainDispatcherExtension::class)
annotation class ViewModelCoroutineTest
