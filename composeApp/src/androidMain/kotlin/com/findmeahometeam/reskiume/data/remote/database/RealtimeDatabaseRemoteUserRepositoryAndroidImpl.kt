package com.findmeahometeam.reskiume.data.remote.database

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
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

class RealtimeDatabaseRemoteUserRepositoryAndroidImpl(
    private val log: Log
) : RealtimeDatabaseRemoteUserRepository {

    private val databaseRef: DatabaseReference =
        Firebase.database.also { it.setPersistenceEnabled(true) }.reference


    override suspend fun insertRemoteUser(
        remoteUser: RemoteUser,
        onInsertRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        remoteUser.uid?.let {
            databaseRef.child(Paths.USERS.path).child(it).setValue(remoteUser.toMap())
                .addOnSuccessListener {
                    onInsertRemoteUser(DatabaseResult.Success)
                }.addOnFailureListener { e ->
                    log.e("RealtimeDatabaseRepositoryAndroidImpl", "insertRemoteUser: Error inserting the remote user ${remoteUser.uid}: ${e.message}")
                    onInsertRemoteUser(DatabaseResult.Error(e.message ?: ""))
                }
        } ?: onInsertRemoteUser(DatabaseResult.Error()).also {
            log.e("RealtimeDatabaseRepositoryAndroidImpl", "insertRemoteUser: Error inserting a remote user")
        }
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> = callbackFlow {
        val userListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val remoteUser: RemoteUser? = dataSnapshot.getValue<RemoteUser>()
                trySend(remoteUser)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                log.w(
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

    override suspend fun updateRemoteUser(
        remoteUser: RemoteUser,
        onUpdateRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val remoteUserValues: Map<String, Any?> = remoteUser.toMap()
        val childUpdates: HashMap<String, Any> =
            hashMapOf("/${Paths.USERS.path}/${remoteUser.uid}" to remoteUserValues)

        databaseRef.updateChildren(childUpdates).addOnSuccessListener {
            onUpdateRemoteUser(DatabaseResult.Success)
        }.addOnFailureListener { e ->
            log.e("RealtimeDatabaseRepositoryAndroidImpl", "updateRemoteUser: Error updating the remote user ${remoteUser.uid}: ${e.message}")
            onUpdateRemoteUser(DatabaseResult.Error(e.message ?: ""))
        }
    }

    override fun deleteRemoteUser(uid: String, onDeleteRemoteUser: (result: DatabaseResult) -> Unit) {
        databaseRef.child(Paths.USERS.path).child(uid).removeValue { error, _ ->
            if (error == null) {
                onDeleteRemoteUser(DatabaseResult.Success)
            } else {
                log.e("RealtimeDatabaseRepositoryAndroidImpl", "deleteRemoteUser: Error deleting the user $uid")
                onDeleteRemoteUser(DatabaseResult.Error())
            }
        }
    }
}
