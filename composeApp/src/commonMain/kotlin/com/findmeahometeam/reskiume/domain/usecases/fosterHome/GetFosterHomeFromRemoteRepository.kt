package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class GetFosterHomeFromRemoteRepository(private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository) {
    operator fun invoke(id: String): Flow<FosterHome> =
        fireStoreRemoteFosterHomeRepository.getRemoteFosterHome(id).mapNotNull { it?.toDomain() }
}
