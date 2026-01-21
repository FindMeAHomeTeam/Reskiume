package com.findmeahometeam.reskiume.data.remote.fireStore

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FireStoreRemoteFosterHomeRepositoryImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val log: Log
) : FireStoreRemoteFosterHomeRepository {

    override suspend fun insertRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onInsertRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        if (remoteFosterHome.id?.isNotBlank() == true
            && remoteFosterHome.ownerId?.isNotBlank() == true
        ) {
            firebaseFirestore
                .collection(Section.FOSTER_HOMES.path)
                .document(remoteFosterHome.id)
                .set(remoteFosterHome.toMap())
                .addOnSuccessListener {

                    onInsertRemoteFosterHome(DatabaseResult.Success)
                }.addOnFailureListener { e ->
                    log.e(
                        "FireStoreRemoteFosterHomeRepositoryImpl",
                        "insertRemoteFosterHome: Error inserting the remote foster home ${remoteFosterHome.id}: ${e.message}"
                    )
                    onInsertRemoteFosterHome(DatabaseResult.Error(e.message ?: ""))
                }
        } else {
            onInsertRemoteFosterHome(DatabaseResult.Error()).also {
                log.e(
                    "FireStoreRemoteFosterHomeRepositoryImpl",
                    "insertRemoteFosterHome: Error inserting a remote foster home with empty id or owner id"
                )
            }
        }
    }

    override suspend fun modifyRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onModifyRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val remoteFosterHomeValues: Map<String, Any?> = remoteFosterHome.toMap()
        firebaseFirestore
            .collection(Section.FOSTER_HOMES.path)
            .document(remoteFosterHome.id!!)
            .update(remoteFosterHomeValues)
            .addOnSuccessListener {

                onModifyRemoteFosterHome(DatabaseResult.Success)
            }.addOnFailureListener { e ->
                log.e(
                    "FireStoreRemoteFosterHomeRepositoryImpl",
                    "modifyRemoteFosterHome: Error updating the remote foster home ${remoteFosterHome.id}: ${e.message}"
                )
                onModifyRemoteFosterHome(DatabaseResult.Error(e.message ?: ""))
            }
    }

    override suspend fun deleteRemoteFosterHome(
        id: String,
        ownerId: String,
        onDeleteRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        firebaseFirestore
            .collection(Section.FOSTER_HOMES.path)
            .document(id)
            .delete()
            .addOnSuccessListener {

                onDeleteRemoteFosterHome(DatabaseResult.Success)
            }.addOnFailureListener { e ->
                log.e(
                    "FireStoreRemoteFosterHomeRepositoryImpl",
                    "deleteRemoteFosterHome: Error deleting the remote foster home $id from the owner $ownerId: ${e.message}"
                )
                onDeleteRemoteFosterHome(DatabaseResult.Error())
            }
    }

    override suspend fun deleteAllMyRemoteFosterHomes(
        ownerId: String,
        onDeleteAllMyRemoteFosterHomes: (result: DatabaseResult) -> Unit
    ) {
        firebaseFirestore
            .collection(Section.FOSTER_HOMES.path)
            .whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener {

                it.forEach { document: QueryDocumentSnapshot ->
                    document.reference.delete()
                }
                onDeleteAllMyRemoteFosterHomes(DatabaseResult.Success)
            }.addOnFailureListener { e ->
                log.e(
                    "FireStoreRemoteFosterHomeRepositoryImpl",
                    "deleteAllMyRemoteFosterHomes: Error deleting all remote foster homes from the owner $ownerId: ${e.message}"
                )
                onDeleteAllMyRemoteFosterHomes(DatabaseResult.Error())
            }
    }

    override fun getRemoteFosterHome(
        id: String,
        ownerId: String
    ): Flow<RemoteFosterHome?> = flow {
        try {
            val querySnapshot = firebaseFirestore
                .collection(Section.FOSTER_HOMES.path)
                .document(id)
                .get()
                .await()

            val result: RemoteFosterHome? = querySnapshot.toObject(RemoteFosterHome::class.java)
            emit(result)

        } catch (e: Exception) {
            log.e(
                "FireStoreRemoteFosterHomeRepositoryImpl",
                "getRemoteFosterHome: Error retrieving the remote foster home $id from the owner $ownerId: ${e.message}"
            )
            emit(null)
        }
    }

    override fun getAllMyRemoteFosterHomes(ownerId: String): Flow<List<RemoteFosterHome?>> = flow {
        try {
            val querySnapshot = firebaseFirestore
                .collection(Section.FOSTER_HOMES.path)
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()

            val result: List<RemoteFosterHome?> =
                querySnapshot.documents.map { documentSnapshot: DocumentSnapshot ->
                    documentSnapshot.toObject(RemoteFosterHome::class.java)
                }
            emit(result)

        } catch (e: Exception) {
            log.e(
                "FireStoreRemoteFosterHomeRepositoryImpl",
                "getAllMyRemoteFosterHomes: Error retrieving all remote foster homes from the owner $ownerId: ${e.message}"
            )
            emit(emptyList())
        }
    }

    override fun getAllRemoteFosterHomesByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RemoteFosterHome?>> = flow {
        try {
            val querySnapshot = firebaseFirestore
                .collection(Section.FOSTER_HOMES.path)
                .whereEqualTo("country", country)
                .whereEqualTo("city", city)
                .get()
                .await()

            val result: List<RemoteFosterHome?> =
                querySnapshot.documents.map { documentSnapshot: DocumentSnapshot ->
                    documentSnapshot.toObject(RemoteFosterHome::class.java)
                }
            emit(result)

        } catch (e: Exception) {
            log.e(
                "FireStoreRemoteFosterHomeRepositoryImpl",
                "getAllRemoteFosterHomesByCountryAndCity: Error retrieving all remote foster homes by country and city ($country, $city): ${e.message}"
            )
            emit(emptyList())
        }
    }

    override fun getAllRemoteFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteFosterHome?>> = flow {
        try {
            val querySnapshot = firebaseFirestore
                .collection(Section.FOSTER_HOMES.path)
                .whereGreaterThanOrEqualTo("longitude", activistLongitude - rangeLongitude)
                .whereLessThanOrEqualTo("longitude", activistLongitude + rangeLongitude)
                .whereGreaterThanOrEqualTo("latitude", activistLatitude - rangeLatitude)
                .whereLessThanOrEqualTo("latitude", activistLatitude + rangeLatitude)
                .get()
                .await()

            val result: List<RemoteFosterHome?> =
                querySnapshot.documents.map { documentSnapshot: DocumentSnapshot ->
                    documentSnapshot.toObject(RemoteFosterHome::class.java)
                }
            emit(result)

        } catch (e: Exception) {
            log.e(
                "FireStoreRemoteFosterHomeRepositoryImpl",
                "getAllRemoteFosterHomesByCountryAndCity: Error retrieving all remote foster homes by location: ${e.message}"
            )
            emit(emptyList())
        }
    }
}
