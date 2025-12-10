package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
    private val remoteNonHumanAnimalList: MutableList<RemoteNonHumanAnimal> = mutableListOf()
) : RealtimeDatabaseRemoteNonHumanAnimalRepository {

    override suspend fun insertRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onInsertRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    ) {
        val nonHumanAnimal =
            remoteNonHumanAnimalList.firstOrNull { it.id == remoteNonHumanAnimal.id }
        if (nonHumanAnimal == null) {
            remoteNonHumanAnimalList.add(remoteNonHumanAnimal)
            onInsertRemoteNonHumanAnimal(DatabaseResult.Success)
        } else {
            onInsertRemoteNonHumanAnimal(DatabaseResult.Error("error adding a non human animal"))
        }
    }

    override suspend fun modifyRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onModifyRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    ) {
        val nonHumanAnimal =
            remoteNonHumanAnimalList.firstOrNull { it.id == remoteNonHumanAnimal.id }
        if (nonHumanAnimal == null) {
            onModifyRemoteNonHumanAnimal(DatabaseResult.Error("error modifying a non human animal"))
        } else {
            remoteNonHumanAnimalList[remoteNonHumanAnimalList.indexOf(nonHumanAnimal)] =
                remoteNonHumanAnimal
            onModifyRemoteNonHumanAnimal(DatabaseResult.Success)
        }
    }

    override fun deleteRemoteNonHumanAnimal(
        id: Int,
        caregiverId: String,
        onDeleteRemoteNonHumanAnimal: (result: DatabaseResult) -> Unit
    ) {
        val nonHumanAnimal =
            remoteNonHumanAnimalList.firstOrNull { it.id == id && it.caregiverId == caregiverId }
        if (nonHumanAnimal == null) {
            onDeleteRemoteNonHumanAnimal(DatabaseResult.Error("error deleting a non human animal"))
        } else {
            remoteNonHumanAnimalList.remove(nonHumanAnimal)
            onDeleteRemoteNonHumanAnimal(DatabaseResult.Success)
        }
    }

    override fun deleteAllRemoteNonHumanAnimals(
        caregiverId: String,
        onDeleteAllRemoteNonHumanAnimals: (result: DatabaseResult) -> Unit
    ) {
        val nonHumanAnimalList = remoteNonHumanAnimalList.filter { it.caregiverId == caregiverId }
        if (nonHumanAnimalList.isEmpty()) {
            onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Error("error deleting all non human animals"))
        } else {
            remoteNonHumanAnimalList.removeAll(nonHumanAnimalList)
            onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Success)
        }
    }

    override fun getRemoteNonHumanAnimal(
        id: Int,
        caregiverId: String
    ): Flow<RemoteNonHumanAnimal?> =
        flowOf(remoteNonHumanAnimalList.firstOrNull { it.id == id && it.caregiverId == caregiverId })

    override fun getAllRemoteNonHumanAnimals(caregiverId: String): Flow<List<RemoteNonHumanAnimal>> =
        flowOf(remoteNonHumanAnimalList.filter { it.caregiverId == caregiverId })
}
