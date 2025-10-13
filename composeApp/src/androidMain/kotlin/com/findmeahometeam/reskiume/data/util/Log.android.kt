package com.findmeahometeam.reskiume.data.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

actual object Log {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
        Firebase.crashlytics.log(message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
        Firebase.crashlytics.recordException(throwable ?: Exception(message))
    }
}
