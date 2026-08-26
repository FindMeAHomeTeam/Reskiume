package com.findmeahometeam.reskiume.data.remote.database.remoteNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealtimeDatabaseRemoteNonHumanAnimalRepositoryIosImpl(
    private val realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper: RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper,
    private val realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate
) : RealtimeDatabaseRemoteNonHumanAnimalRepository {

    private suspend fun initialCheck(
        onSuccess: suspend (RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override suspend fun insertRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onInsertRemoteNonHumanAnimal: (DatabaseResult) -> Unit
    ) {
        if (remoteNonHumanAnimal.caregiverId.isNullOrBlank()) return onInsertRemoteNonHumanAnimal(DatabaseResult.Error())

        initialCheck(
            onSuccess = {
                it.insertRemoteNonHumanAnimal(remoteNonHumanAnimal, onInsertRemoteNonHumanAnimal)
            },
            onFailure = {
                onInsertRemoteNonHumanAnimal(DatabaseResult.Error())
            }
        )
    }

    override suspend fun modifyRemoteNonHumanAnimal(
        remoteNonHumanAnimal: RemoteNonHumanAnimal,
        onModifyRemoteNonHumanAnimal: (DatabaseResult) -> Unit
    ) {
        if (remoteNonHumanAnimal.caregiverId.isNullOrBlank()) return onModifyRemoteNonHumanAnimal(DatabaseResult.Error())

        initialCheck(
            onSuccess = {
                it.modifyRemoteNonHumanAnimal(remoteNonHumanAnimal, onModifyRemoteNonHumanAnimal)
            },
            onFailure = {
                onModifyRemoteNonHumanAnimal(DatabaseResult.Error())
            }
        )
    }

    override fun deleteRemoteNonHumanAnimal(
        id: String,
        caregiverId: String,
        onDeleteRemoteNonHumanAnimal: (DatabaseResult) -> Unit
    ) {
        if (id.isBlank() || caregiverId.isBlank()) return onDeleteRemoteNonHumanAnimal(DatabaseResult.Error())

        val value =
            realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteNonHumanAnimal(id, caregiverId, onDeleteRemoteNonHumanAnimal)
        } else {
            onDeleteRemoteNonHumanAnimal(DatabaseResult.Error())
        }
    }

    override fun deleteAllRemoteNonHumanAnimals(
        caregiverId: String,
        onDeleteAllRemoteNonHumanAnimals: (DatabaseResult) -> Unit
    ) {
        if (caregiverId.isBlank()) return onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Error())

        val value =
            realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper.realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteAllRemoteNonHumanAnimals(caregiverId, onDeleteAllRemoteNonHumanAnimals)
        } else {
            onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Error())
        }
    }

    override fun getRemoteNonHumanAnimal(
        id: String,
        caregiverId: String
    ): Flow<RemoteNonHumanAnimal?> {
        realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate
            .updateNonHumanAnimalIdAndCaregiverId(id, caregiverId)
        return realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate.remoteNonHumanAnimalListFlow.map { it.firstOrNull() }
    }

    override fun getAllRemoteNonHumanAnimals(caregiverId: String): Flow<List<RemoteNonHumanAnimal>> {
        realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate
            .updateNonHumanAnimalIdAndCaregiverId(caregiverId = caregiverId)
        return realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate.remoteNonHumanAnimalListFlow
    }
}
