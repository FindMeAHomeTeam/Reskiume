package com.findmeahometeam.reskiume.data.remote.storage

import android.content.Context
import androidx.core.net.toUri
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import java.io.File

class StorageRepositoryAndroidImpl(
    private val context: Context
) : StorageRepository {

    private val storageRef: StorageReference = Firebase.storage.reference

    private fun getStorageReference(
        section: Section,
        userUid: String,
        extraId: String = ""
    ): StorageReference {
        return when {
            section == Section.USERS -> storageRef
                .child(Section.USERS.path)
                .child(userUid)
                .child("$userUid.webp")

            section == Section.NON_HUMAN_ANIMALS -> storageRef
                .child(Section.NON_HUMAN_ANIMALS.path)
                .child(userUid)
                .child("$extraId.webp")

            else -> storageRef
                .child(Section.USERS.path)
                .child(userUid)
                .child("$userUid.webp")
        }
    }

    override fun uploadImage(
        userUid: String,
        extraId: String,
        section: Section,
        imageUri: String,
        onImageUploaded: (imagePath: String) -> Unit
    ) {
        val imageRef: StorageReference = getStorageReference(section, userUid, extraId)
        val uploadTask: UploadTask = imageRef.putFile(imageUri.toUri())
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onImageUploaded(task.result.toString())
            } else {
                onImageUploaded("")
            }
        }
    }

    override fun downloadImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageSaved: (imagePath: String) -> Unit
    ) {
        val imageRef: StorageReference = getStorageReference(section, userUid, extraId)
        val localFile = when {
            section == Section.USERS -> File(context.filesDir, "$userUid.webp")
            section == Section.NON_HUMAN_ANIMALS -> File(context.filesDir, "$userUid$extraId.webp")
            else -> File(context.filesDir, "$userUid.webp")
        }

        // Ensure the parent directory exists
        localFile.parentFile?.apply { if (!exists()) mkdirs() }

        imageRef.getFile(localFile).addOnSuccessListener {
            onImageSaved(localFile.absolutePath)
        }.addOnFailureListener {
            onImageSaved("")
        }
    }

    override fun deleteLocalImage(
        currentImagePath: String,
        onImageDeleted: (isDeleted: Boolean) -> Unit
    ) {
        val localFile = if (currentImagePath.contains("file:///")) {
            val uri = currentImagePath.toUri()
            File(uri.path ?: return onImageDeleted(false))
        } else {
            val filename = currentImagePath.split("/").last()
            File(context.filesDir, filename)
        }
        onImageDeleted(localFile.delete())
    }

    override suspend fun deleteRemoteImage(
        userUid: String,
        extraId: String,
        section: Section,
        onImageDeleted: (isDeleted: Boolean) -> Unit
    ) {
        val imageRef: StorageReference = getStorageReference(section, userUid, extraId)
        imageRef.delete().addOnSuccessListener {
            onImageDeleted(true)
        }.addOnFailureListener {
            onImageDeleted(false)
        }
    }
}
