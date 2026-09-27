package com.fstengineering.leasilycrm.testUtils

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fstengineering.leasilycrm.core.di.modules.daoModule
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.testUtils.fakes.FakeAppDateTimeProvider
import org.junit.After
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

/**
 * Base de teste instrumentado (`docs/guidelines/0004-integration-tests.md`): a cada caso de teste,
 * sobe o Koin com banco Room in-memory novo e [AppDateTimeProvider] fake, e os descarta ao fim.
 * O Koin do app não sobe nos testes: o [IntegrationTestRunner] usa o [IntegrationTestApplication].
 */
abstract class IntegrationTest : KoinTest {
    protected val appDateTimeProvider = FakeAppDateTimeProvider()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(daoModule, integrationTestModule())
    }

    protected val db: AppDatabase by inject()

    @After
    fun closeDatabase() {
        db.close()
    }

    private fun integrationTestModule() = module {
        single<AppDateTimeProvider> { appDateTimeProvider }
        single<AppDatabase> {
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext<Context>(),
                AppDatabase::class.java,
            ).build()
        }
    }
}
