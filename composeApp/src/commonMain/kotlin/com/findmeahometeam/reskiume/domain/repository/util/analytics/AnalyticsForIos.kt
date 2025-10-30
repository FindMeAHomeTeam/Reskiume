package com.findmeahometeam.reskiume.domain.repository.util.analytics

interface AnalyticsForIos {
    fun logEvent(message: String, params: Map<String, Any?>?)
}
