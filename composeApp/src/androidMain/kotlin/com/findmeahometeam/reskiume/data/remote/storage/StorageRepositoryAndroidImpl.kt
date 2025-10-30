package com.findmeahometeam.reskiume.data.remote.storage

import android.content.Context
import androidx.core.net.toUri
import com.findmeahometeam.reskiume.data.util.Paths
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

    private fun getStorageReference(imageType: Paths, userUid: String): StorageReference {
        return when (imageType) {
            Paths.USERS -> storageRef.child(Paths.USERS.path).child(userUid).child("$userUid.webp")
        }
    }

    override fun uploadImage(
        userUid: String,
        imageType: Paths,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        val imageRef: StorageReference = getStorageReference(imageType, userUid)
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

    override fun saveImage(userUid: String, imageType: Paths, onImageSaved: (String) -> Unit) {
        val imageRef: StorageReference = getStorageReference(imageType, userUid)
        val localFile = when(imageType) {
            Paths.USERS -> File(context.filesDir, "$userUid.webp")
        }

        // Ensure the parent directory exists
        localFile.parentFile?.apply { if(!exists()) mkdirs() }

        imageRef.getFile(localFile).addOnSuccessListener {
            onImageSaved(localFile.absolutePath)
        }.addOnFailureListener {
            onImageSaved("")
        }
    }

    override suspend fun deleteImage(
        userUid: String,
        imageType: Paths,
        onImageDeleted: (Boolean) -> Unit
    ) {
        val imageRef: StorageReference = getStorageReference(imageType, userUid)
        imageRef.delete().addOnSuccessListener {
            onImageDeleted(true)
        }.addOnFailureListener {
            onImageDeleted(false)
        }
    }
}
