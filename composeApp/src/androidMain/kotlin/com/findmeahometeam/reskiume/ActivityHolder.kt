package com.findmeahometeam.reskiume

import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

/**
 * Call [attach] from MainActivity.onCreate() and [detach] from MainActivity.onDestroy().
 * [detach] is a no-op if the activity being destroyed is not the one currently stored
 * (handles configuration-change recreation safely).
 */
object ActivityHolder {
    private var _activity: WeakReference<ComponentActivity>? = null

    val activityOrNull: ComponentActivity?
        get() = _activity?.get()

    fun attach(activity: ComponentActivity) {
        _activity = WeakReference(activity)
    }

    /**
     * Only clears the reference if [activity] is the same instance that was stored.
     * This prevents a newly-attached activity from being nulled out by the old instance's
     * onDestroy (which can happen during configuration changes).
     */
    fun detach(activity: ComponentActivity) {
        if (_activity?.get() === activity) {
            _activity = null
        }
    }
}
