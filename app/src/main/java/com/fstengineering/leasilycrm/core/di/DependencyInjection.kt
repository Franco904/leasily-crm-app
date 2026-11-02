package com.fstengineering.leasilycrm.core.di

import android.app.Application
import com.fstengineering.leasilycrm.assistanceRegistration.di.assistanceRegistrationRepositoryModule
import com.fstengineering.leasilycrm.assistanceRegistration.di.assistanceRegistrationUseCaseModule
import com.fstengineering.leasilycrm.assistanceRegistration.di.assistanceRegistrationViewModelModule
import com.fstengineering.leasilycrm.core.di.modules.applicationServiceModule
import com.fstengineering.leasilycrm.core.di.modules.coroutineModule
import com.fstengineering.leasilycrm.core.di.modules.daoModule
import com.fstengineering.leasilycrm.core.di.modules.databaseDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.inMemoryDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.preferencesDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.presentationModule
import com.fstengineering.leasilycrm.core.di.modules.remoteDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.repositoryModule
import com.fstengineering.leasilycrm.core.di.modules.sharedStorageDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.useCaseModule
import com.fstengineering.leasilycrm.core.di.modules.workerModule
import com.fstengineering.leasilycrm.dashboard.di.dashboardRepositoryModule
import com.fstengineering.leasilycrm.dashboard.di.dashboardViewModelModule
import com.fstengineering.leasilycrm.home.di.homeRepositoryModule
import com.fstengineering.leasilycrm.home.di.homeViewModelModule
import com.fstengineering.leasilycrm.leadTenantDetails.di.leadTenantDetailsRepositoryModule
import com.fstengineering.leasilycrm.leadTenantDetails.di.leadTenantDetailsUseCaseModule
import com.fstengineering.leasilycrm.leadTenantDetails.di.leadTenantDetailsViewModelModule
import com.fstengineering.leasilycrm.leadTenantRegistration.di.leadTenantRegistrationRepositoryModule
import com.fstengineering.leasilycrm.leadTenantRegistration.di.leadTenantRegistrationUseCaseModule
import com.fstengineering.leasilycrm.leadTenantRegistration.di.leadTenantRegistrationViewModelModule
import com.fstengineering.leasilycrm.leadTenants.di.leadTenantsRepositoryModule
import com.fstengineering.leasilycrm.leadTenants.di.leadTenantsViewModelModule
import com.fstengineering.leasilycrm.occupiedPeriodDetails.di.occupiedPeriodDetailsRepositoryModule
import com.fstengineering.leasilycrm.occupiedPeriodDetails.di.occupiedPeriodDetailsUseCaseModule
import com.fstengineering.leasilycrm.occupiedPeriodDetails.di.occupiedPeriodDetailsViewModelModule
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.di.occupiedPeriodRegistrationRepositoryModule
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.di.occupiedPeriodRegistrationUseCaseModule
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.di.occupiedPeriodRegistrationViewModelModule
import com.fstengineering.leasilycrm.occupiedPeriods.di.occupiedPeriodsRepositoryModule
import com.fstengineering.leasilycrm.occupiedPeriods.di.occupiedPeriodsViewModelModule
import com.fstengineering.leasilycrm.profile.di.profileRepositoryModule
import com.fstengineering.leasilycrm.profile.di.profileUseCaseModule
import com.fstengineering.leasilycrm.profile.di.profileViewModelModule
import com.fstengineering.leasilycrm.imoveis.di.imoveisRepositoryModule
import com.fstengineering.leasilycrm.imoveis.di.imoveisViewModelModule
import com.fstengineering.leasilycrm.imovelDashboard.di.imovelDashboardDataSourceModule
import com.fstengineering.leasilycrm.imovelDashboard.di.imovelDashboardRepositoryModule
import com.fstengineering.leasilycrm.imovelDashboard.di.imovelDashboardUseCaseModule
import com.fstengineering.leasilycrm.imovelDashboard.di.imovelDashboardViewModelModule
import com.fstengineering.leasilycrm.imovelDetails.di.imovelDetailsRepositoryModule
import com.fstengineering.leasilycrm.imovelDetails.di.imovelDetailsViewModelModule
import com.fstengineering.leasilycrm.imovelOccupation.di.imovelOccupationRepositoryModule
import com.fstengineering.leasilycrm.imovelOccupation.di.imovelOccupationUseCaseModule
import com.fstengineering.leasilycrm.imovelOccupation.di.imovelOccupationViewModelModule
import com.fstengineering.leasilycrm.proprietarioDetails.di.proprietarioDetailsDataSourceModule
import com.fstengineering.leasilycrm.proprietarioDetails.di.proprietarioDetailsViewModelModule
import com.fstengineering.leasilycrm.proprietarioRegistration.di.proprietarioRegistrationDataSourceModule
import com.fstengineering.leasilycrm.proprietarioRegistration.di.proprietarioRegistrationViewModelModule
import com.fstengineering.leasilycrm.proprietarios.di.proprietariosDataSourceModule
import com.fstengineering.leasilycrm.proprietarios.di.proprietariosViewModelModule
import com.fstengineering.leasilycrm.imovelRegistration.di.imovelRegistrationDataSourceModule
import com.fstengineering.leasilycrm.imovelRegistration.di.imovelRegistrationRepositoryModule
import com.fstengineering.leasilycrm.imovelRegistration.di.imovelRegistrationUseCaseModule
import com.fstengineering.leasilycrm.imovelRegistration.di.imovelRegistrationViewModelModule
import com.fstengineering.leasilycrm.root.di.rootViewModelModule
import com.fstengineering.leasilycrm.settings.di.settingsViewModelModule
import com.fstengineering.leasilycrm.signin.di.signinRepositoryModule
import com.fstengineering.leasilycrm.signin.di.signinUseCaseModule
import com.fstengineering.leasilycrm.signin.di.signinViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
    startKoin {
        androidLogger()
        androidContext(this@configureDependencyInjection)
        workManagerFactory()

        modules(
            // Core shared kernel
            presentationModule,
            repositoryModule,
            useCaseModule,
            applicationServiceModule,
            databaseDataSourceModule,
            inMemoryDataSourceModule,
            preferencesDataSourceModule,
            remoteDataSourceModule,
            sharedStorageDataSourceModule,
            daoModule,
            coroutineModule,
            workerModule,

            // Feature: root
            rootViewModelModule,

            // Feature: signin
            signinRepositoryModule,
            signinUseCaseModule,
            signinViewModelModule,

            // Feature: settings
            settingsViewModelModule,

            // Feature: profile
            profileRepositoryModule,
            profileUseCaseModule,
            profileViewModelModule,

            // Feature: home
            homeRepositoryModule,
            homeViewModelModule,

            // Feature: userAnalytics
            dashboardRepositoryModule,
            dashboardViewModelModule,

            // Feature: imoveis
            imoveisRepositoryModule,
            imoveisViewModelModule,

            // Feature: imovelRegistration
            imovelRegistrationDataSourceModule,
            imovelRegistrationRepositoryModule,
            imovelRegistrationUseCaseModule,
            imovelRegistrationViewModelModule,

            // Feature: imovelDashboard
            imovelDashboardDataSourceModule,
            imovelDashboardRepositoryModule,
            imovelDashboardUseCaseModule,
            imovelDashboardViewModelModule,

            // Feature: imovelDetails
            imovelDetailsRepositoryModule,
            imovelDetailsViewModelModule,

            // Feature: imovelOccupation
            imovelOccupationRepositoryModule,
            imovelOccupationUseCaseModule,
            imovelOccupationViewModelModule,

            // Feature: proprietarios
            proprietariosDataSourceModule,
            proprietariosViewModelModule,

            // Feature: proprietarioRegistration
            proprietarioRegistrationDataSourceModule,
            proprietarioRegistrationViewModelModule,

            // Feature: proprietarioDetails
            proprietarioDetailsDataSourceModule,
            proprietarioDetailsViewModelModule,

            // Feature: leadTenants
            leadTenantsRepositoryModule,
            leadTenantsViewModelModule,

            // Feature: leadTenantRegistration
            leadTenantRegistrationRepositoryModule,
            leadTenantRegistrationUseCaseModule,
            leadTenantRegistrationViewModelModule,

            // Feature: leadTenantDetails
            leadTenantDetailsRepositoryModule,
            leadTenantDetailsUseCaseModule,
            leadTenantDetailsViewModelModule,

            // Feature: assistanceRegistration
            assistanceRegistrationRepositoryModule,
            assistanceRegistrationUseCaseModule,
            assistanceRegistrationViewModelModule,

            // Feature: occupiedPeriods
            occupiedPeriodsRepositoryModule,
            occupiedPeriodsViewModelModule,

            // Feature: occupiedPeriodRegistration
            occupiedPeriodRegistrationRepositoryModule,
            occupiedPeriodRegistrationUseCaseModule,
            occupiedPeriodRegistrationViewModelModule,

            // Feature: occupiedPeriodDetails
            occupiedPeriodDetailsRepositoryModule,
            occupiedPeriodDetailsUseCaseModule,
            occupiedPeriodDetailsViewModelModule,
        )
    }
}
