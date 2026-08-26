package com.findmeahometeam.reskiume.data.util.log

import com.findmeahometeam.reskiume.domain.repository.util.log.CrashlyticsForIos
import com.findmeahometeam.reskiume.domain.repository.util.log.CrashlyticsForIosWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CrashlyticsForIosDelegateWrapperImpl : CrashlyticsForIosWrapper {

    private val _crashlyticsForIosDelegateState: MutableStateFlow<CrashlyticsForIos?> = MutableStateFlow(null)

    override val crashlyticsForIosDelegateState: StateFlow<CrashlyticsForIos?> =
        _crashlyticsForIosDelegateState.asStateFlow()


    override fun updateCrashlyticsForIosDelegate(delegate: CrashlyticsForIos?) {
        _crashlyticsForIosDelegateState.value = delegate
    }
}
