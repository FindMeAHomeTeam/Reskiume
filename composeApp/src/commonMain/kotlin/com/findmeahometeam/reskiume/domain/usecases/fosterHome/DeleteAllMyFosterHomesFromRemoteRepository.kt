package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository

class DeleteAllMyFosterHomesFromRemoteRepository(private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository) {

    suspend operator fun invoke(
        ownerId: String,
        onDeleteAllMyRemoteFosterHomes: (result: DatabaseResult) -> Unit
    ) {
        fireStoreRemoteFosterHomeRepository.deleteAllMyRemoteFosterHomes(ownerId, onDeleteAllMyRemoteFosterHomes)
    }
}
