package com.findmeahometeam.reskiume.domain.usecases.image

import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class DeleteImageFromLocalDataSource(private val storageRepository: StorageRepository) {

    operator fun invoke(
        userUid: String,
        currentImagePath: String,
        onImageDeleted: (Boolean) -> Unit
    ) {
        if (currentImagePath.isBlank()) {
            onImageDeleted(true)
        } else {
            storageRepository.deleteLocalImage(userUid, currentImagePath, onImageDeleted)
        }
    }
}
