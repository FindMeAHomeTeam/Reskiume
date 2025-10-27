package com.findmeahometeam.reskiume.data.remote.storage

import androidx.core.net.toUri
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage

class StorageRepositoryAndroidImpl : StorageRepository {

    private val storageRef: StorageReference = Firebase.storage.reference

    override fun uploadImage(
        userUid: String,
        imageType: Paths,
        imageUri: String,
        onImageUploaded: (String) -> Unit
    ) {
        val imageRef: StorageReference = when (imageType) {
            Paths.USERS -> storageRef.child(Paths.USERS.path).child(userUid).child("$userUid.webp")
        }
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

    override fun saveImage(userUid: String, imageType: Paths, imageUri: String) {

    }

    override fun deleteImage(
        userUid: String,
        imageType: Paths,
        onImageDeleted: (Boolean) -> Unit
    ) {
        val imageRef: StorageReference = when (imageType) {
            Paths.USERS -> storageRef.child(Paths.USERS.path).child(userUid).child("$userUid.webp")
        }
        imageRef.delete().addOnSuccessListener {
            onImageDeleted(true)
        }.addOnFailureListener {
            onImageDeleted(false)
        }
    }
}