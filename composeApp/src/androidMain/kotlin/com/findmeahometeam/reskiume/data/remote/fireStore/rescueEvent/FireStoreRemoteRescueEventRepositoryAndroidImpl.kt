package com.findmeahometeam.reskiume.data.remote.fireStore.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.collections.forEach

class FireStoreRemoteRescueEventRepositoryAndroidImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val log: Log
) : FireStoreRemoteRescueEventRepository {

    override suspend fun insertRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onInsertRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        if (remoteRescueEvent.id?.isNotBlank() == true
            && remoteRescueEvent.creatorId?.isNotBlank() == true
        ) {
            firebaseFirestore
                .collection(Section.RESCUE_EVENTS.path)
                .document(remoteRescueEvent.id)
                .set(remoteRescueEvent.toMap())
                .addOnSuccessListener {

                    onInsertRemoteRescueEvent(DatabaseResult.Success)
                }.addOnFailureListener { e ->
                    log.e(
                        "FireStoreRemoteRescueEventRepositoryAndroidImpl",
                        "insertRemoteRescueEvent: Error inserting the remote rescue event ${remoteRescueEvent.id}: ${e.message}"
                    )
                    onInsertRemoteRescueEvent(DatabaseResult.Error(e.message ?: ""))
                }
        } else {
            onInsertRemoteRescueEvent(DatabaseResult.Error()).also {
                log.e(
                    "FireStoreRemoteRescueEventRepositoryAndroidImpl",
                    "insertRemoteRescueEvent: Error inserting a remote rescue event with empty id or creator id"
                )
            }
        }
    }

    override suspend fun modifyRemoteRescueEvent(
        remoteRescueEvent: RemoteRescueEvent,
        onModifyRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        val remoteRescueEventValues: Map<String, Any?> = remoteRescueEvent.toMap()
        firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .document(remoteRescueEvent.id!!)
            .update(remoteRescueEventValues)
            .addOnSuccessListener {

                onModifyRemoteRescueEvent(DatabaseResult.Success)
            }.addOnFailureListener { e ->
                log.e(
                    "FireStoreRemoteRescueEventRepositoryAndroidImpl",
                    "modifyRemoteRescueEvent: Error updating the remote rescue event ${remoteRescueEvent.id}: ${e.message}"
                )
                onModifyRemoteRescueEvent(DatabaseResult.Error(e.message ?: ""))
            }
    }

    override suspend fun deleteRemoteRescueEvent(
        id: String,
        onDeleteRemoteRescueEvent: (result: DatabaseResult) -> Unit
    ) {
        firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .document(id)
            .delete()
            .addOnSuccessListener {

                onDeleteRemoteRescueEvent(DatabaseResult.Success)
            }.addOnFailureListener { e ->
                log.e(
                    "FireStoreRemoteRescueEventRepositoryAndroidImpl",
                    "deleteRemoteRescueEvent: Error deleting the remote rescue event $id: ${e.message}"
                )
                onDeleteRemoteRescueEvent(DatabaseResult.Error())
            }
    }

    override suspend fun deleteAllMyRemoteRescueEvents(
        creatorId: String,
        onDeleteAllMyRemoteRescueEvents: (result: DatabaseResult) -> Unit
    ) {
        firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .whereEqualTo("creatorId", creatorId)
            .get()
            .addOnSuccessListener {

                it.forEach { document: QueryDocumentSnapshot ->
                    document.reference.delete()
                }
                onDeleteAllMyRemoteRescueEvents(DatabaseResult.Success)
            }.addOnFailureListener { e ->
                log.e(
                    "FireStoreRemoteRescueEventRepositoryAndroidImpl",
                    "deleteAllMyRemoteRescueEvents: Error deleting all remote rescue events from the creatorId $creatorId: ${e.message}"
                )
                onDeleteAllMyRemoteRescueEvents(DatabaseResult.Error())
            }
    }

    override fun getRemoteRescueEvent(id: String): Flow<RemoteRescueEvent?> = flow {

        val querySnapshot = firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .document(id)
            .get()
            .await()

        val result: RemoteRescueEvent? = querySnapshot.toObject(RemoteRescueEvent::class.java)
        emit(result)

    }.catch { e ->
        log.e(
            "FireStoreRemoteRescueEventRepositoryAndroidImpl",
            "getRemoteRescueEvent: Error retrieving the remote rescue event $id: ${e.message}"
        )
        emit(null)
    }

    override fun getAllMyRemoteRescueEvents(creatorId: String): Flow<List<RemoteRescueEvent?>> = flow {
        val querySnapshot = firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .whereEqualTo("creatorId", creatorId)
            .get()
            .await()

        val result: List<RemoteRescueEvent?> =
            querySnapshot.documents.map { documentSnapshot: DocumentSnapshot ->
                documentSnapshot.toObject(RemoteRescueEvent::class.java)
            }
        emit(result)

    }.catch { e ->
        log.e(
            "FireStoreRemoteRescueEventRepositoryAndroidImpl",
            "getAllMyRemoteRescueEvents: Error retrieving all remote rescue events from the creatorId $creatorId: ${e.message}"
        )
        emit(emptyList())
    }

    override fun getAllRemoteRescueEventsByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RemoteRescueEvent?>> = flow {
        val querySnapshot = firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .whereEqualTo("country", country)
            .whereEqualTo("city", city)
            .get()
            .await()

        val result: List<RemoteRescueEvent?> =
            querySnapshot.documents.map { documentSnapshot: DocumentSnapshot ->
                documentSnapshot.toObject(RemoteRescueEvent::class.java)
            }
        emit(result)

    }.catch { e ->
        log.e(
            "FireStoreRemoteRescueEventRepositoryAndroidImpl",
            "getAllRemoteRescueEventsByCountryAndCity: Error retrieving all remote rescue events by country and city ($country, $city): ${e.message}"
        )
        emit(emptyList())
    }

    override fun getAllRemoteRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteRescueEvent?>> = flow {
        val querySnapshot = firebaseFirestore
            .collection(Section.RESCUE_EVENTS.path)
            .whereGreaterThanOrEqualTo("longitude", activistLongitude - rangeLongitude)
            .whereLessThanOrEqualTo("longitude", activistLongitude + rangeLongitude)
            .whereGreaterThanOrEqualTo("latitude", activistLatitude - rangeLatitude)
            .whereLessThanOrEqualTo("latitude", activistLatitude + rangeLatitude)
            .get()
            .await()

        val result: List<RemoteRescueEvent?> =
            querySnapshot.documents.map { documentSnapshot: DocumentSnapshot ->
                documentSnapshot.toObject(RemoteRescueEvent::class.java)
            }
        emit(result)

    }.catch { e ->
        log.e(
            "FireStoreRemoteRescueEventRepositoryAndroidImpl",
            "getAllRemoteRescueEventsByLocation: Error retrieving all remote rescue events by location: ${e.message}"
        )
        emit(emptyList())
    }
}
