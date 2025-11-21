package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class UploadImageToRemoteDataSource(private val storageRepository: StorageRepository) {

    operator fun invoke(
        userUid: String,
        imageType: Section,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        if (imageUri.isBlank()) {
            onImageUploaded("")
        } else {
            storageRepository.uploadImage(userUid, imageType, imageUri, onImageUploaded)
        }
    }
}
