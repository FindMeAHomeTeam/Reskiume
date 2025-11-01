package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class DeleteImageInLocalDataSource(private val storageRepository: StorageRepository) {

    operator fun invoke(
        userUid: String,
        imageType: Paths,
        onImageDeleted: (Boolean) -> Unit
    ) {
        storageRepository.deleteLocalImage(userUid, imageType, onImageDeleted)
    }
}
