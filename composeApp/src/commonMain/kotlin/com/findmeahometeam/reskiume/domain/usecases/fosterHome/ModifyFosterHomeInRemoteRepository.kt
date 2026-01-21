package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository

class ModifyFosterHomeInRemoteRepository(private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository) {

    suspend operator fun invoke(
        fosterHome: FosterHome,
        onModifyRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        fireStoreRemoteFosterHomeRepository.modifyRemoteFosterHome(
            fosterHome.toData(),
            onModifyRemoteFosterHome
        )
    }
}
