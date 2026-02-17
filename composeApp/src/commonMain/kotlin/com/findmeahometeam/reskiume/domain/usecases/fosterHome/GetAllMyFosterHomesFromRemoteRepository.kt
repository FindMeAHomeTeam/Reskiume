package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllMyFosterHomesFromRemoteRepository(private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository) {

    operator fun invoke(ownerId: String): Flow<List<FosterHome>> =
        fireStoreRemoteFosterHomeRepository.getAllMyRemoteFosterHomes(ownerId)
            .map { list: List<RemoteFosterHome?> ->
                list.mapNotNull {
                    it?.toDomain()
                }
            }
}
