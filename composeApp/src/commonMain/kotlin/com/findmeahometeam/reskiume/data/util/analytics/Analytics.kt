package com.findmeahometeam.reskiume.data.util.analytics

expect object Analytics {
    fun logEvent(name: String, params: Map<Any?, *>? = null)
}
