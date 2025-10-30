package com.findmeahometeam.reskiume.data.remote.storage

import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepositoryForIosDelegateWrapper

class StorageRepositoryIosImpl(
    private val storageRepositoryForIosDelegateWrapper: StorageRepositoryForIosDelegateWrapper
): StorageRepository {

    private fun initialCheck(
        onSuccess: (StorageRepository) -> Unit,
        onFailure: () -> Unit
    ) {
        val value: StorageRepository? = storageRepositoryForIosDelegateWrapper.storageRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override fun uploadImage(
        userUid: String,
        imageType: Paths,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        initialCheck(
            onSuccess = {
                it.uploadImage(userUid, imageType, imageUri, onImageUploaded)
            },
            onFailure = {
                onImageUploaded("")
            }
        )
    }

    override fun saveImage(
        userUid: String,
        imageType: Paths,
        onImageSaved: (String) -> Unit
    ) {
        initialCheck(
            onSuccess = {
                it.saveImage(userUid, imageType, onImageSaved)
            },
            onFailure = {
                onImageSaved("")
            }
        )
    }

    override suspend fun deleteImage(
        userUid: String,
        imageType: Paths,
        onImageDeleted: (Boolean) -> Unit
    ) {
        val value: StorageRepository? = storageRepositoryForIosDelegateWrapper.storageRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteImage(userUid, imageType, onImageDeleted)
        } else {
            onImageDeleted(false)
        }
    }
}