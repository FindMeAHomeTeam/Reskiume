package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetAllFosterHomesByCountryAndCityFromRemoteRepository(
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) {
    operator fun invoke(
        country: String,
        city: String,
    ): Flow<List<FosterHome>> =
        fireStoreRemoteFosterHomeRepository.getAllRemoteFosterHomesByCountryAndCity(country, city)
            .map { list: List<RemoteFosterHome?> ->
                list.mapNotNull { remoteFosterHome: RemoteFosterHome? ->

                    remoteFosterHome?.toDomain(
                        onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                            checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                nonHumanAnimalId = nonHumanAnimalId,
                                caregiverId = caregiverId
                            ).firstOrNull()
                        }
                    )
                }
            }
}
