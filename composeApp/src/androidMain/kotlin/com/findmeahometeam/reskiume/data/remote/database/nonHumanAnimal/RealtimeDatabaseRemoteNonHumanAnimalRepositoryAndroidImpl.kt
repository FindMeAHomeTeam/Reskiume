package com.findmeahometeam.reskiume.data.remote.database.nonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.nonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl(
    private val databaseRef: DatabaseReference,
    private val log: Log
) : RealtimeDatabaseRemoteNonHumanAnimalRepository {

    override suspend fun insertRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onInsertRemoteNonHumanAnimal: (DatabaseResult) -> Unit
    ) {
        if (remoteNonHumanAnimal.id.isNotEmpty()) {
            databaseRef
                .child(Section.NON_HUMAN_ANIMALS.path)
                .child(remoteNonHumanAnimal.caregiverId)
                .child(remoteNonHumanAnimal.id)
                .setValue(remoteNonHumanAnimal.toMap())
                .addOnSuccessListener {
                    onInsertRemoteNonHumanAnimal(DatabaseResult.Success)
                }.addOnFailureListener { e ->
                    log.e(
                        "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                        "insertRemoteNonHumanAnimal: Error inserting the remote non human animal ${remoteNonHumanAnimal.id}: ${e.message}"
                    )
                    onInsertRemoteNonHumanAnimal(DatabaseResult.Error(e.message ?: ""))
                }
        } else {
            onInsertRemoteNonHumanAnimal(DatabaseResult.Error()).also {
                log.e(
                    "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                    "insertRemoteNonHumanAnimal: Error inserting a remote non human animal with empty id"
                )
            }
        }
    }

    override suspend fun modifyRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onModifyRemoteNonHumanAnimal: (DatabaseResult) -> Unit
    ) {
        val remoteNonHumanAnimalValues: Map<String, Any?> = remoteNonHumanAnimal.toMap()
        val childUpdates: HashMap<String, Any> =
            hashMapOf("/${Section.NON_HUMAN_ANIMALS.path}/${remoteNonHumanAnimal.caregiverId}" to remoteNonHumanAnimalValues)

        databaseRef.updateChildren(childUpdates).addOnSuccessListener {
            onModifyRemoteNonHumanAnimal(DatabaseResult.Success)
        }.addOnFailureListener { e ->
            log.e(
                "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                "modifyRemoteNonHumanAnimal: Error updating the remote non human animal ${remoteNonHumanAnimal.id}: ${e.message}"
            )
            onModifyRemoteNonHumanAnimal(DatabaseResult.Error(e.message ?: ""))
        }
    }

    override suspend fun deleteRemoteNonHumanAnimal(
        id: String,
        caregiverId: String,
        onDeleteRemoteNonHumanAnimal: (DatabaseResult) -> Unit
    ) {
        databaseRef
            .child(Section.NON_HUMAN_ANIMALS.path)
            .child(caregiverId)
            .child(id)
            .removeValue { error, _ ->

                if (error == null) {
                    onDeleteRemoteNonHumanAnimal(DatabaseResult.Success)
                } else {
                    log.e(
                        "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                        "deleteRemoteNonHumanAnimal: Error deleting the non human animal from the caregiver $caregiverId"
                    )
                    onDeleteRemoteNonHumanAnimal(DatabaseResult.Error())
                }
            }
    }

    override suspend fun deleteAllRemoteNonHumanAnimals(
        caregiverId: String,
        onDeleteAllRemoteNonHumanAnimals: (DatabaseResult) -> Unit
    ) {
        databaseRef
            .child(Section.NON_HUMAN_ANIMALS.path)
            .child(caregiverId)
            .removeValue { error, _ ->

                if (error == null) {
                    onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Success)
                } else {
                    log.e(
                        "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                        "deleteAllRemoteNonHumanAnimals: Error deleting all non human animals from the caregiver $caregiverId"
                    )
                    onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Error())
                }
            }
    }

    override fun getRemoteNonHumanAnimal(
        id: String,
        caregiverId: String
    ): Flow<RemoteNonHumanAnimal?> =
        callbackFlow {
            val nonHumanAnimalListener: ValueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val remoteNonHumanAnimal: RemoteNonHumanAnimal? =
                        dataSnapshot.getValue<RemoteNonHumanAnimal>()

                    trySend(remoteNonHumanAnimal)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    log.e(
                        "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                        "getRemoteNonHumanAnimal:onCancelled ${databaseError.toException()}"
                    )
                }
            }
            databaseRef
                .child(Section.NON_HUMAN_ANIMALS.path)
                .child(caregiverId)
                .child(id)
                .addListenerForSingleValueEvent(nonHumanAnimalListener)

            awaitClose {
                databaseRef
                    .child(Section.NON_HUMAN_ANIMALS.path)
                    .child(caregiverId)
                    .child(id)
                    .removeEventListener(nonHumanAnimalListener)
            }
        }

    override fun getAllRemoteNonHumanAnimals(caregiverId: String): Flow<List<RemoteNonHumanAnimal>> =
        callbackFlow {
            val nonHumanAnimalListener: ValueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val remoteNonHumanAnimal: List<RemoteNonHumanAnimal> =
                        dataSnapshot.children.mapNotNull { it.getValue<RemoteNonHumanAnimal>() }

                    trySend(remoteNonHumanAnimal)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    log.e(
                        "RealtimeDatabaseRemoteNonHumanAnimalRepositoryAndroidImpl",
                        "getAllRemoteNonHumanAnimals:onCancelled ${databaseError.toException()}"
                    )
                }
            }
            databaseRef
                .child(Section.NON_HUMAN_ANIMALS.path)
                .child(caregiverId)
                .addListenerForSingleValueEvent(nonHumanAnimalListener)

            awaitClose {
                databaseRef
                    .child(Section.NON_HUMAN_ANIMALS.path)
                    .child(caregiverId)
                    .removeEventListener(nonHumanAnimalListener)
            }
        }
}
