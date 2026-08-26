package com.findmeahometeam.reskiume.data.util.analytics

import com.findmeahometeam.reskiume.domain.repository.util.analytics.AnalyticsForIos
import com.findmeahometeam.reskiume.domain.repository.util.analytics.AnalyticsForIosWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnalyticsForIosDelegateWrapperImpl : AnalyticsForIosWrapper {

    private val _analyticsForIosForIosDelegateState: MutableStateFlow<AnalyticsForIos?> = MutableStateFlow(null)

    override val analyticsForIosDelegateState: StateFlow<AnalyticsForIos?> =
        _analyticsForIosForIosDelegateState.asStateFlow()


    override fun updateAnalyticsForIosDelegate(delegate: AnalyticsForIos?) {
        _analyticsForIosForIosDelegateState.value = delegate
    }
}
