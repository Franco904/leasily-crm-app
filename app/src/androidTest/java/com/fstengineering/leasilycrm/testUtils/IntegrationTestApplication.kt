package com.fstengineering.leasilycrm.testUtils

import android.app.Application

/**
 * Application dos testes instrumentados: não sobe o Koin nem agenda alarmes/notificações, deixando o
 * grafo de dependências inteiramente a cargo de cada [IntegrationTest].
 */
class IntegrationTestApplication : Application()
