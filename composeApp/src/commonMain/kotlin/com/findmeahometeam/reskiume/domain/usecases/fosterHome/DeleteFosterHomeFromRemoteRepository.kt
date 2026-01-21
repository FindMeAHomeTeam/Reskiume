package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository

class DeleteFosterHomeFromRemoteRepository(private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository) {

    suspend operator fun invoke(
        id: String,
        ownerId: String,
        onDeleteRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        fireStoreRemoteFosterHomeRepository.deleteRemoteFosterHome(
            id,
            ownerId,
            onDeleteRemoteFosterHome
        )
    }
}
