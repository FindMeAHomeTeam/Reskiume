package com.findmeahometeam.reskiume.domain.repository.remote.database

import kotlinx.coroutines.flow.StateFlow

interface RealtimeDatabaseRepositoryForIosDelegateWrapper {
    val realtimeDatabaseRepositoryForIosDelegateState: StateFlow<RealtimeDatabaseRepositoryForIosDelegate?>
    fun updateRealtimeDatabaseRepositoryForIosDelegate(delegate: RealtimeDatabaseRepositoryForIosDelegate?)
}
