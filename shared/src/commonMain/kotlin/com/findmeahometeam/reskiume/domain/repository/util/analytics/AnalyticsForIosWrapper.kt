package com.findmeahometeam.reskiume.domain.repository.util.analytics

import kotlinx.coroutines.flow.StateFlow

interface AnalyticsForIosWrapper {
    val analyticsForIosDelegateState: StateFlow<AnalyticsForIos?>
    fun updateAnalyticsForIosDelegate(delegate: AnalyticsForIos?)
}
