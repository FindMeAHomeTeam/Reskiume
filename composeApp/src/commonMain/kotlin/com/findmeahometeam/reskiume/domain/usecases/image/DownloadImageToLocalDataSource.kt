package com.findmeahometeam.reskiume.domain.usecases.image

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DownloadImageToLocalDataSource(private val storageRepository: StorageRepository) {

    suspend operator fun invoke(
        userUid: String,
        extraId: String,
        section: Section,
    ): String {
        return suspendCoroutine { continuation ->

            storageRepository.downloadImage(userUid, extraId, section) { imagePath ->

                continuation.resume(imagePath)
            }
        }
    }
}
