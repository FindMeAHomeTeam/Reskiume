package com.findmeahometeam.reskiume.domain.usecases.image

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class DownloadImageToLocalDataSource(private val storageRepository: StorageRepository) {

    operator fun invoke(
        userUid: String,
        extraId: String,
        section: Section,
        onImageSaved: (String) -> Unit
    ) {
        storageRepository.downloadImage(userUid, extraId, section, onImageSaved)
    }
}
