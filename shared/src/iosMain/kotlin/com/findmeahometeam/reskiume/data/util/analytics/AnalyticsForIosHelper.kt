package com.findmeahometeam.reskiume.data.util.analytics

import com.findmeahometeam.reskiume.domain.repository.util.analytics.AnalyticsForIosWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AnalyticsForIosHelper: KoinComponent {
    val analyticsForIosWrapper: AnalyticsForIosWrapper by inject()
}
