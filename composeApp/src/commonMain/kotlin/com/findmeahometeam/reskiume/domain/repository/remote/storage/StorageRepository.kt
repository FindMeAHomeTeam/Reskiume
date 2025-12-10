package com.findmeahometeam.reskiume.domain.repository.remote.storage

import com.findmeahometeam.reskiume.data.util.Section

interface StorageRepository {
    fun uploadImage(userUid: String, extraId: String = "", section: Section, imageUri: String, onImageUploaded: (String) -> Unit)
    fun downloadImage(userUid: String, extraId: String = "", section: Section, onImageSaved: (String) -> Unit)
    fun deleteLocalImage(currentImagePath: String, onImageDeleted: (Boolean) -> Unit)
    suspend fun deleteRemoteImage(userUid: String, extraId: String = "", section: Section, onImageDeleted: (Boolean) -> Unit)
}
