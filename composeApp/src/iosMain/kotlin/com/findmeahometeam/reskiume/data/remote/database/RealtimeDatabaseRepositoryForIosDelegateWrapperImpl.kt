package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RealtimeDatabaseRepositoryForIosDelegateWrapperImpl :
    RealtimeDatabaseRepositoryForIosDelegateWrapper {

    private val _realtimeDatabaseRepositoryForIosDelegateState: MutableStateFlow<RealtimeDatabaseRepositoryForIosDelegate?> =
        MutableStateFlow(null)

    override val realtimeDatabaseRepositoryForIosDelegateState: StateFlow<RealtimeDatabaseRepositoryForIosDelegate?> =
        _realtimeDatabaseRepositoryForIosDelegateState.asStateFlow()

    override fun updateRealtimeDatabaseRepositoryForIosDelegate(delegate: RealtimeDatabaseRepositoryForIosDelegate?) {
        _realtimeDatabaseRepositoryForIosDelegateState.value = delegate
    }
}
