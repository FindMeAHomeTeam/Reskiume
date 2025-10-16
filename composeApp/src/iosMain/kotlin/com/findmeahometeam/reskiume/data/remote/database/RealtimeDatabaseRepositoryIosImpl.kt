package com.findmeahometeam.reskiume.data.remote.database

import cocoapods.FirebaseDatabase.FIRDataEventType
import cocoapods.FirebaseDatabase.FIRDataSnapshot
import cocoapods.FirebaseDatabase.FIRDatabase
import cocoapods.FirebaseDatabase.FIRDatabaseHandle
import cocoapods.FirebaseDatabase.FIRDatabaseReference
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.remote.response.toRemoteUser
import com.findmeahometeam.reskiume.data.util.Log
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.data.util.Paths
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSError

@OptIn(ExperimentalForeignApi::class)
class RealtimeDatabaseRepositoryIosImpl : RealtimeDatabaseRepository {

    private val databaseRef: FIRDatabaseReference =
        FIRDatabase.database().also { it.setPersistenceEnabled(true) }.reference()

    override fun insertRemoteUser(
        remoteUser: RemoteUser,
        onInsertRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        remoteUser.uid?.let {
            databaseRef.child(Paths.USERS.path).child(it).setValue(
                value = remoteUser.toMap(),
                withCompletionBlock = { error: NSError?, _ ->
                    if (error == null) {
                        onInsertRemoteUser(DatabaseResult.Success)
                    } else {
                        onInsertRemoteUser(DatabaseResult.Error("Error: ${error.localizedDescription}"))
                    }
                }
            )
        } ?: onInsertRemoteUser(DatabaseResult.Error("Error inserting a remote user"))
    }

    override fun getRemoteUser(uid: String): Flow<RemoteUser?> = callbackFlow {
        val handle: FIRDatabaseHandle =
            databaseRef.child(Paths.USERS.path).child(uid).observeEventType(
                eventType = FIRDataEventType.FIRDataEventTypeValue,
                withBlock = { snapshot: FIRDataSnapshot? ->
                    val dict: Map<String, Any?>? = snapshot?.value as? Map<String, Any?>
                    trySend(dict?.toRemoteUser())
                },
                withCancelBlock = { error: NSError? ->
                    Log.w(
                        "RealtimeDatabaseRepositoryIos",
                        "getRemoteUser:onCancelled ${error?.localizedDescription}"
                    )
                    close()
                }
            )

        awaitClose {
            databaseRef.removeObserverWithHandle(handle)
        }
    }

    override fun updateRemoteUser(
        remoteUser: RemoteUser,
        onUpdateRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        val key: String? = databaseRef.child(Paths.USERS.path).childByAutoId().key
        if (key == null) {
            Log.w("RealtimeDatabaseRepositoryIos", "Couldn't get push key for users")
            return
        }
        val remoteUserValues: Map<String, Any?> = remoteUser.toMap()
        val childUpdates: Map<Any?, *> = mapOf("/${Paths.USERS.path}/$key" to remoteUserValues)

        databaseRef.updateChildValues(
            values = childUpdates,
            withCompletionBlock = { error: NSError?, _: FIRDatabaseReference? ->
                if (error == null) {
                    onUpdateRemoteUser(DatabaseResult.Success)
                } else {
                    onUpdateRemoteUser(DatabaseResult.Error("Error updating the remote user ${remoteUser.uid}: ${error.localizedDescription}"))
                }
            }
        )
    }

    override fun deleteRemoteUser(
        uid: String,
        onDeleteRemoteUser: (result: DatabaseResult) -> Unit
    ) {
        databaseRef.child(Paths.USERS.path).child(uid).removeValueWithCompletionBlock { error, _ ->
            if (error == null) {
                onDeleteRemoteUser(DatabaseResult.Success)
            } else {
                Log.w("RealtimeDatabaseRepositoryIos", "Error deleting the user $uid")
                onDeleteRemoteUser(DatabaseResult.Error("Error deleting the remote user $uid"))
            }
        }
    }
}
