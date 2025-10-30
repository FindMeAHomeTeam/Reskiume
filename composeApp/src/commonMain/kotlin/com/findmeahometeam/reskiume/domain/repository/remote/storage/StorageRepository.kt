package com.findmeahometeam.reskiume.domain.repository.remote.storage

import com.findmeahometeam.reskiume.data.util.Paths

interface StorageRepository {
    fun uploadImage(userUid: String, imageType: Paths, imageUri: String, onImageUploaded: (String) -> Unit)
    fun saveImage(userUid: String, imageType: Paths, onImageSaved: (String) -> Unit)

    suspend fun deleteImage(userUid: String, imageType: Paths, onImageDeleted: (Boolean) -> Unit)
}
