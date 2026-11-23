package com.fstengineering.leasilycrm.core.infra.adapters

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Guarda a `Activity` em primeiro plano, para que adapters que abrem telas do sistema (Credential
 * Manager) não exijam a `Activity` na assinatura de quem os chama — ViewModels e UseCases ficam
 * sem tipo do Android. Registrado em `Application.registerActivityLifecycleCallbacks`.
 */
class CurrentActivityTracker : Application.ActivityLifecycleCallbacks {
    private var currentActivity: WeakReference<Activity>? = null

    /** `Activity` em primeiro plano, ou `null` quando o app não tem tela retomada. */
    fun current(): Activity? = currentActivity?.get()

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity?.get() === activity) currentActivity = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
