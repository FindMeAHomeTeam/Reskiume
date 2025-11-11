package com.findmeahometeam.reskiume.data.util.analytics

import android.os.Bundle
import com.findmeahometeam.reskiume.data.util.log.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class AnalyticsAndroidImpl(
    private val log: Log
) : Analytics {
    override fun logEvent(
        name: String,
        params: Map<Any?, *>?
    ) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key.toString(), value)
                is Int -> bundle.putInt(key.toString(), value)
                is Long -> bundle.putLong(key.toString(), value)
                is Double -> bundle.putDouble(key.toString(), value)
                is Float -> bundle.putFloat(key.toString(), value)
                is Boolean -> bundle.putBoolean(key.toString(), value)
                else -> bundle.putString(key.toString(), value?.toString())
            }
        }
        Firebase.analytics.logEvent(name, bundle)
        log.d("Analytics", "Event logged: $name with params: $params")
    }
}
