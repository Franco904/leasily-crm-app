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
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

/**
 * Base de teste instrumentado (`docs/guidelines/0004-integration-tests.md`): a cada caso de teste,
 * sobe o Koin com banco Room in-memory novo e [AppDateTimeProvider] fake, e os descarta ao fim.
 */
abstract class IntegrationTest : KoinTest {
    protected val appDateTimeProvider = FakeAppDateTimeProvider()

    @get:Rule
    val koinTestRule: TestRule = RuleChain
        // O Application do app já inicia o Koin; é preciso pará-lo antes de subir o do teste.
        .outerRule(object : ExternalResource() {
            override fun before() = stopKoin()
        })
        .around(
            KoinTestRule.create {
                androidContext(ApplicationProvider.getApplicationContext())
                modules(daoModule, integrationTestModule())
            }
        )

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
