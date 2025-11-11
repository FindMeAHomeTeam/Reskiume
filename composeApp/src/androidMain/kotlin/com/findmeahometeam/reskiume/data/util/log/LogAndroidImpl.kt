package com.findmeahometeam.reskiume.data.util.log

import android.util.Log
import com.findmeahometeam.reskiume.data.util.log.Log as LogInterface
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class LogAndroidImpl: LogInterface {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
        Firebase.crashlytics.log(message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
        Firebase.crashlytics.recordException(throwable ?: Exception(message))
    }
}
