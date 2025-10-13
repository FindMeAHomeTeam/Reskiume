package com.findmeahometeam.reskiume.data.util

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class)
actual object Log {
    actual fun d(tag: String, message: String) {
        NSLog("$tag: $message")
        FIRCrashlytics.crashlytics().log(message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("$tag: $message - ${throwable?.message ?: ""}")
        val nsError = throwable as? NSError ?: NSError.errorWithDomain(
            domain = "$tag: $message - ${throwable?.message ?: ""}",
            code = 0,
            userInfo = mapOf(
                "NSLocalizedDescriptionKey" to message,
                "NSLocalizedFailureReasonErrorKey" to (throwable?.message ?: "Unknown error"),
                "error_details" to "$tag: $message"
            )
        )
        FIRCrashlytics.crashlytics().recordError(nsError)
    }
}
