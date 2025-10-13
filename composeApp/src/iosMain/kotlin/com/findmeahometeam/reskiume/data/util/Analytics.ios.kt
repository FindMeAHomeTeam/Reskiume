package com.findmeahometeam.reskiume.data.util

import cocoapods.FirebaseAnalytics.FIRAnalytics
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class)
actual object Analytics {
    actual fun logEvent(
        name: String,
        params: Map<Any?, *>?
    ) {
        FIRAnalytics.logEventWithName(name = name, parameters = params)
        NSLog("Analytics - Event logged: $name with params: $params")
    }
}
