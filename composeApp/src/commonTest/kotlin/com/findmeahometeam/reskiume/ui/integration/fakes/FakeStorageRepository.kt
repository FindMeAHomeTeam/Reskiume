package com.findmeahometeam.reskiume.ui.integration.fakes

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class FakeStorageRepository(
    private val remoteDatasourceList: MutableList<Pair<String, String>> = mutableListOf(),
    private val localDatasourceList: MutableList<Pair<String, String>> = mutableListOf()
) : StorageRepository {

    override fun uploadImage(
        userUid: String,
        section: Section,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        remoteDatasourceList.add(Pair("$userUid/${section.path}", imageUri))
        onImageUploaded(imageUri)
    }

    override fun saveImage(
        userUid: String,
        section: Section,
        onImageSaved: (String) -> Unit
    ) {
        val pathToLocalImage = "local_path/$userUid/${section.path}"
        localDatasourceList.add(Pair("$userUid/${section.path}", pathToLocalImage))
        onImageSaved(pathToLocalImage)
    }

    override fun deleteLocalImage(
        userUid: String,
        currentImagePath: String,
        onImageDeleted: (Boolean) -> Unit
    ) {
        localDatasourceList.firstOrNull { it.first == "$userUid/$currentImagePath" }?.let {
            localDatasourceList.remove(it)
            onImageDeleted(true)
        } ?: onImageDeleted(false)
    }

    override suspend fun deleteRemoteImage(
        userUid: String,
        section: Section,
        onImageDeleted: (Boolean) -> Unit
    ) {
        remoteDatasourceList.firstOrNull { it.first == "$userUid/${section.path}" }?.let {
            remoteDatasourceList.remove(it)
            onImageDeleted(true)
        } ?: onImageDeleted(false)
    }
}
