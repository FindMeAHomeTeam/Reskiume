package com.findmeahometeam.reskiume.domain.repository.remote.storage

import kotlinx.coroutines.flow.StateFlow

interface StorageRepositoryForIosDelegateWrapper {
    val storageRepositoryForIosDelegateState: StateFlow<StorageRepository?>
    fun updateStorageRepositoryForIosDelegate(delegate: StorageRepository?)
}