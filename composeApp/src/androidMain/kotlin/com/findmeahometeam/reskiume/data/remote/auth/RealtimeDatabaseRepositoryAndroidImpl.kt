package com.findmeahometeam.reskiume.data.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Log
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.data.util.Paths
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeDatabaseRepositoryAndroidImpl : RealtimeDatabaseRepository {

    private val databaseRef: DatabaseReference =
        Firebase.database.also { it.setPersistenceEnabled(true) }.reference


    override fun insertRemoteUser(
        remoteUser: RemoteUser,
        onInsertRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        remoteUser.uid?.let {
            databaseRef.child(Paths.USERS.path).child(it).setValue(remoteUser.toMap())
                .addOnSuccessListener {
                    onInsertRemoteUser(DatabaseResult.Success)
                }.addOnFailureListener { e ->
                    onInsertRemoteUser(DatabaseResult.Error("Error inserting the remote user ${remoteUser.uid}: ${e.message}"))
                }
        } ?: onInsertRemoteUser(DatabaseResult.Error("Error inserting a remote user"))
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> = callbackFlow {
        val userListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val remoteUser: RemoteUser? = dataSnapshot.getValue<RemoteUser>()
                trySend(remoteUser)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "RealtimeDatabaseRepositoryAndroid",
                    "getRemoteUser:onCancelled ${databaseError.toException()}"
                )
            }
        }
        databaseRef.child(Paths.USERS.path).child(uid).addListenerForSingleValueEvent(userListener)
        awaitClose {
            databaseRef.child(Paths.USERS.path).child(uid).removeEventListener(userListener)
        }
    }

    override fun updateRemoteUser(
        remoteUser: RemoteUser,
        onUpdateRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val key: String? = databaseRef.child(Paths.USERS.path).push().key
        if (key == null) {
            Log.w("RealtimeDatabaseRepositoryAndroid", "Couldn't get push key for users")
            return
        }
        val remoteUserValues: Map<String, Any?> = remoteUser.toMap()
        val childUpdates: HashMap<String, Any> =
            hashMapOf("/${Paths.USERS.path}/$key" to remoteUserValues)

        databaseRef.updateChildren(childUpdates).addOnSuccessListener {
            onUpdateRemoteUser(DatabaseResult.Success)
        }.addOnFailureListener { e ->
            onUpdateRemoteUser(DatabaseResult.Error("Error updating the remote user ${remoteUser.uid}: ${e.message}"))
        }
    }

    override fun deleteRemoteUser(uid: String, onDeleteRemoteUser: (result: DatabaseResult) -> Unit) {
        databaseRef.child(Paths.USERS.path).child(uid).removeValue { error, _ ->
            if (error == null) {
                onDeleteRemoteUser(DatabaseResult.Success)
            } else {
                Log.w("RealtimeDatabaseRepositoryAndroid", "Error deleting the user $uid")
                onDeleteRemoteUser(DatabaseResult.Error("Error deleting the remote user $uid"))
            }
        }
    }
}
