package com.findmeahometeam.reskiume.domain.usecases.image

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class DeleteImageFromRemoteDataSource(private val storageRepository: StorageRepository) {

    suspend operator fun invoke(
        userUid: String,
        extraId: String,
        section: Section,
        currentUserImage: String,
        onImageDeleted: (Boolean) -> Unit
    ) {
        if (currentUserImage.isBlank()) {
            onImageDeleted(true)
        } else {
            storageRepository.deleteRemoteImage(userUid, extraId, section, onImageDeleted)
        }
    }
}
