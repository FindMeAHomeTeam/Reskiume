package com.findmeahometeam.reskiume.data.util.log

import com.findmeahometeam.reskiume.domain.repository.util.log.CrashlyticsForIosWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CrashlyticsForIosHelper: KoinComponent {
    val crashlyticsForIosWrapper: CrashlyticsForIosWrapper by inject()
}
