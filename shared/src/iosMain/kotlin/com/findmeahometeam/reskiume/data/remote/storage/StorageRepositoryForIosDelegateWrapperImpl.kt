package com.findmeahometeam.reskiume.data.remote.storage

import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StorageRepositoryForIosDelegateWrapperImpl : StorageRepositoryForIosDelegateWrapper {

    private val _storageRepositoryForIosDelegateState: MutableStateFlow<StorageRepository?> =
        MutableStateFlow(null)

    override val storageRepositoryForIosDelegateState: StateFlow<StorageRepository?> =
        _storageRepositoryForIosDelegateState.asStateFlow()

    override fun updateStorageRepositoryForIosDelegate(delegate: StorageRepository?) {
        _storageRepositoryForIosDelegateState.value = delegate
    }
}
