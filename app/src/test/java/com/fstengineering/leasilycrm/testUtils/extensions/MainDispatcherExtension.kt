package com.fstengineering.leasilycrm.testUtils.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactoryContext
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback

/**
 * Substitui `Dispatchers.Main` por um [TestDispatcher] antes da construção da instância de teste
 * (cobre SUT que lança corrotina no `init`) e antes de cada teste; restaura ao fim da classe.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherExtension(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestInstancePreConstructCallback, BeforeEachCallback, AfterAllCallback {
    override fun preConstructTestInstance(
        factoryContext: TestInstanceFactoryContext,
        context: ExtensionContext,
    ) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun beforeEach(context: ExtensionContext) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterAll(context: ExtensionContext) {
        Dispatchers.resetMain()
    }
}
