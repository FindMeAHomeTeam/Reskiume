package com.findmeahometeam.reskiume.data.util.log

import com.findmeahometeam.reskiume.domain.repository.util.log.CrashlyticsForIosWrapper
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class)
actual object Log: KoinComponent {

    private val crashlyticsForIosWrapper: CrashlyticsForIosWrapper by inject()

    actual fun d(tag: String, message: String) {
        NSLog("$tag: $message")
    }

    actual fun w(tag: String, message: String) {
        NSLog("$tag: $message")
        crashlyticsForIosWrapper.crashlyticsForIosDelegateState.value?.log(message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("$tag: $message ${throwable?.message ?: ""}")
        crashlyticsForIosWrapper.crashlyticsForIosDelegateState.value?.logError("$tag: $message ${throwable?.message ?: ""}")
    }
}
