package com.findmeahometeam.reskiume.domain.repository.remote.database.nonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabaseRemoteNonHumanAnimalRepository {
    suspend fun insertRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onInsertRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    )

    suspend fun modifyRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onModifyRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteRemoteNonHumanAnimal(
        id: String,
        caregiverId: String,
        onDeleteRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteAllRemoteNonHumanAnimals(
        caregiverId: String,
        onDeleteAllRemoteNonHumanAnimals: (result: DatabaseResult) -> Unit
    )

    fun getRemoteNonHumanAnimal(id: String, caregiverId: String): Flow<RemoteNonHumanAnimal?>
    fun getAllRemoteNonHumanAnimals(caregiverId: String): Flow<List<RemoteNonHumanAnimal>>
}
