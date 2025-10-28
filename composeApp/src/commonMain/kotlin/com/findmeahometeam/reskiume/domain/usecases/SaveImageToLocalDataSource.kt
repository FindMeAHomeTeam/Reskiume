package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class SaveImageToLocalDataSource(private val storageRepository: StorageRepository) {

    operator fun invoke(
        userUid: String,
        imageType: Paths,
        onImageSaved: (String) -> Unit
    ) {
        storageRepository.saveImage(userUid, imageType, onImageSaved)
    }
}
