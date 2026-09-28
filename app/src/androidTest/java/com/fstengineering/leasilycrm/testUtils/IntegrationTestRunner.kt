package com.fstengineering.leasilycrm.testUtils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/** Runner dos testes instrumentados; instancia [IntegrationTestApplication] no lugar do Application do app. */
class IntegrationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, IntegrationTestApplication::class.java.name, context)
}
