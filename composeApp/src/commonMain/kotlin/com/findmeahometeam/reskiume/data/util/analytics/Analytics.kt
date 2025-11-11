package com.findmeahometeam.reskiume.data.util.analytics

interface Analytics {
    fun logEvent(name: String, params: Map<Any?, *>? = null)
}
