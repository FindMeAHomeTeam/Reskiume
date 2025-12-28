package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository

class FakeStorageRepository(
    private val remoteDatasourceList: MutableList<Pair<String, String>> = mutableListOf(),
    private val localDatasourceList: MutableList<Pair<String, String>> = mutableListOf()
) : StorageRepository {

    override fun uploadImage(
        userUid: String,
        extraId: String,
        section: Section,
        imageUri: String,
        onImageUploaded: (imagePath: String) -> Unit
    ) {
        remoteDatasourceList.add(Pair("${section.path}/$userUid", imageUri))
        onImageUploaded(imageUri)
    }

    override fun downloadImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageSaved: (imagePath: String) -> Unit
    ) {
        localDatasourceList.add(Pair("${section.path}/$userUid", if(extraId.isEmpty()) "$userUid.webp" else "$userUid$extraId.webp"))
        onImageSaved("${section.path}/$userUid")
    }

    override fun deleteLocalImage(
        currentImagePath: String,
        onImageDeleted: (isDeleted: Boolean) -> Unit
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
        onImageDeleted: (isDeleted: Boolean) -> Unit
    ) {
        remoteDatasourceList.firstOrNull { it.first == "${section.path}/$userUid" }?.let {
            remoteDatasourceList.remove(it)
            onImageDeleted(true)
        } ?: onImageDeleted(false)
    }
}
