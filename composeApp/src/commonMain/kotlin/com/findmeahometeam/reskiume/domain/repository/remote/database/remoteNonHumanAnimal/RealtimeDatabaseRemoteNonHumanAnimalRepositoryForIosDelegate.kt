package com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal

interface RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate {
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
}
