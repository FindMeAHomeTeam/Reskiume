package com.findmeahometeam.reskiume.data.util

expect object Analytics {
    fun logEvent(name: String, params: Map<Any?, *>? = null)
}