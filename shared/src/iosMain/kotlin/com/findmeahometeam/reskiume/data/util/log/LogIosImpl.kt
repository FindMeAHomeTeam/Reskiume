package com.findmeahometeam.reskiume.data.util.log

import com.findmeahometeam.reskiume.domain.repository.util.log.CrashlyticsForIosWrapper
import platform.Foundation.NSLog

class LogIosImpl(
    private val crashlyticsForIosWrapper: CrashlyticsForIosWrapper
): Log {

    override fun d(tag: String, message: String) {
        NSLog("$tag: $message")
    }

    override fun w(tag: String, message: String) {
        NSLog("$tag: $message")
        crashlyticsForIosWrapper.crashlyticsForIosDelegateState.value?.log(message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("$tag: $message ${throwable?.message ?: ""}")
        crashlyticsForIosWrapper.crashlyticsForIosDelegateState.value?.logError("$tag: $message ${throwable?.message ?: ""}")
    }
}
