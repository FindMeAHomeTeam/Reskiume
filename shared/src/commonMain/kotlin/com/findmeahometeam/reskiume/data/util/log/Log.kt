package com.findmeahometeam.reskiume.data.util.log

interface Log {
    fun d(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
