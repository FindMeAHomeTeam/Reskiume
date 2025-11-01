package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class DeleteImageFromRemoteDataSource(private val storageRepository: StorageRepository) {

    suspend operator fun invoke(
        userUid: String,
        imageType: Paths,
        onImageDeleted: (Boolean) -> Unit
    ) {
        storageRepository.deleteRemoteImage(userUid, imageType, onImageDeleted)
    }
}
