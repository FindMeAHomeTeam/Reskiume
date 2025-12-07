package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal

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

    fun deleteRemoteNonHumanAnimal(
        id: Int,
        caregiverId: String,
        onDeleteRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    )

    fun deleteAllRemoteNonHumanAnimals(
        caregiverId: String,
        onDeleteAllRemoteNonHumanAnimals: (result: DatabaseResult) -> Unit
    )

    fun getRemoteNonHumanAnimal(id: Int, caregiverId: String): Flow<RemoteNonHumanAnimal?>
    fun getAllRemoteNonHumanAnimals(caregiverId: String): Flow<List<RemoteNonHumanAnimal>>
}
