package com.findmeahometeam.reskiume.data.util.analytics

import com.findmeahometeam.reskiume.domain.repository.util.analytics.AnalyticsForIosWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSLog

actual object Analytics: KoinComponent {

    private val analyticsForIosDelegateWrapper: AnalyticsForIosWrapper by inject()

    actual fun logEvent(
        name: String,
        params: Map<Any?, *>?
    ) {
        // Sanitize keys to String and values to Firebase-allowed primitives
        val sanitizedHashMap = LinkedHashMap<String, Any?>()
        params?.forEach { (key, value) ->
            val myKey: String = key?.toString() ?: return@forEach
            val myValue: Any? = when (value) {
                null -> null
                is String -> value
                is Int, is Long, is Float, is Double, is Boolean -> value
                else -> value.toString()
            }
            sanitizedHashMap[myKey] = myValue
        }
        analyticsForIosDelegateWrapper.analyticsForIosDelegateState.value?.logEvent(name, sanitizedHashMap)
        NSLog("Analytics - Event logged: $name with params: $sanitizedHashMap")
    }
}
