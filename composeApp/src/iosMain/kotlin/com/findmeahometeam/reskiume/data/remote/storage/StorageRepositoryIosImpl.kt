package com.findmeahometeam.reskiume.data.remote.storage

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepositoryForIosDelegateWrapper

class StorageRepositoryIosImpl(
    private val storageRepositoryForIosDelegateWrapper: StorageRepositoryForIosDelegateWrapper
) : StorageRepository {

    private fun initialCheck(
        onSuccess: (StorageRepository) -> Unit,
        onFailure: () -> Unit
    ) {
        val value: StorageRepository? =
            storageRepositoryForIosDelegateWrapper.storageRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override fun uploadImage(
        userUid: String,
        extraId: String,
        section: Section,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        initialCheck(
            onSuccess = {
                it.uploadImage(userUid, extraId, section, imageUri, onImageUploaded)
            },
            onFailure = {
                onImageUploaded("")
            }
        )
    }

    override fun downloadImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageSaved: (String) -> Unit
    ) {
        initialCheck(
            onSuccess = {
                it.downloadImage(userUid, extraId, section, onImageSaved)
            },
            onFailure = {
                onImageSaved("")
            }
        )
    }

    override fun deleteLocalImage(
        currentImagePath: String,
        onImageDeleted: (Boolean) -> Unit
    ) {
        initialCheck(
            onSuccess = {
                it.deleteLocalImage(currentImagePath, onImageDeleted)
            },
            onFailure = {
                onImageDeleted(false)
            }
        )
    }

    override suspend fun deleteRemoteImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageDeleted: (Boolean) -> Unit
    ) {
        val value: StorageRepository? =
            storageRepositoryForIosDelegateWrapper.storageRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteImage(userUid, extraId, section, onImageDeleted)
        } else {
            onImageDeleted(false)
        }
    }
}