package com.findmeahometeam.reskiume.domain.repository.util.log

import kotlinx.coroutines.flow.StateFlow

interface CrashlyticsForIosWrapper {
    val crashlyticsForIosDelegateState: StateFlow<CrashlyticsForIos?>
    fun updateCrashlyticsForIosDelegate(delegate: CrashlyticsForIos?)
}
