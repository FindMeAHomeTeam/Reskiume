package com.findmeahometeam.reskiume.domain.repository.remote.storage

import com.findmeahometeam.reskiume.data.util.Section

interface StorageRepository {
    fun uploadImage(userUid: String, section: Section, imageUri: String, onImageUploaded: (String) -> Unit)
    fun saveImage(userUid: String, section: Section, onImageSaved: (String) -> Unit)
    fun deleteLocalImage(userUid: String, currentImagePath: String, onImageDeleted: (Boolean) -> Unit)
    suspend fun deleteRemoteImage(userUid: String, section: Section, onImageDeleted: (Boolean) -> Unit)
}
