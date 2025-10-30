package com.findmeahometeam.reskiume.data.remote.storage

import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepositoryForIosDelegateWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StorageRepositoryForIosHelper: KoinComponent {
    val storageRepositoryForIosDelegateWrapper: StorageRepositoryForIosDelegateWrapper by inject()
}
