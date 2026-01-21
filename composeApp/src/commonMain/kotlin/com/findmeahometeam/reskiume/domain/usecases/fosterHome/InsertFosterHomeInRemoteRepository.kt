package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository

class InsertFosterHomeInRemoteRepository(private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository) {

    suspend operator fun invoke(
        fosterHome: FosterHome,
        onInsertRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        fireStoreRemoteFosterHomeRepository.insertRemoteFosterHome(
            fosterHome.toData(),
            onInsertRemoteFosterHome
        )
    }
}
