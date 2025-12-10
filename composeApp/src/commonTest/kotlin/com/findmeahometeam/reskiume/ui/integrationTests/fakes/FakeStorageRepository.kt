package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class FakeStorageRepository(
    private val remoteDatasourceList: MutableList<Pair<String, String>> = mutableListOf(),
    private val localDatasourceList: MutableList<Pair<String, String>> = mutableListOf()
) : StorageRepository {

    private fun getPath(section: Section, userUid: String, extraId: String): String {
        return if (extraId.isBlank()) {
            "${section.path}/$userUid"
        } else {
            "${section.path}/$userUid/$extraId"
        }
    }

    override fun uploadImage(
        userUid: String,
        extraId: String,
        section: Section,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        val path = getPath(section, userUid, extraId)
        remoteDatasourceList.add(Pair(path, imageUri))
        onImageUploaded(imageUri)
    }

    override fun downloadImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageSaved: (String) -> Unit
    ) {
        val pathToLocalImage = getPath(section, userUid, extraId)
        localDatasourceList.add(Pair(pathToLocalImage, if(extraId.isEmpty()) "$userUid.webp" else "$extraId.webp"))
        onImageSaved(pathToLocalImage)
    }

    override fun deleteLocalImage(
        currentImagePath: String,
        onImageDeleted: (Boolean) -> Unit
    ) {
        localDatasourceList.firstOrNull { it.second == currentImagePath }?.let {
            localDatasourceList.remove(it)
            onImageDeleted(true)
        } ?: onImageDeleted(false)
    }

    override suspend fun deleteRemoteImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageDeleted: (Boolean) -> Unit
    ) {
        val pathToImage = getPath(section, userUid, extraId)
        remoteDatasourceList.firstOrNull { it.first == pathToImage }?.let {
            remoteDatasourceList.remove(it)
            onImageDeleted(true)
        } ?: onImageDeleted(false)
    }
}
