package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class UploadImageToRemoteDataSource(private val storageRepository: StorageRepository) {

    operator fun invoke(
        userUid: String,
        imageType: Paths,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        storageRepository.uploadImage(userUid, imageType, imageUri, onImageUploaded)
    }
}
