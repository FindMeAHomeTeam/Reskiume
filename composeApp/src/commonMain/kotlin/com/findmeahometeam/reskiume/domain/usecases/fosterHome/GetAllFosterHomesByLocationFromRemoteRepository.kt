package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetAllFosterHomesByLocationFromRemoteRepository(
    private val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
) {
    operator fun invoke(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHome>> =
        fireStoreRemoteFosterHomeRepository.getAllRemoteFosterHomesByLocation(
            activistLongitude,
            activistLatitude,
            rangeLongitude,
            rangeLatitude
        ).map { list: List<RemoteFosterHome?> ->
            list.mapNotNull { remoteFosterHome: RemoteFosterHome? ->

                remoteFosterHome?.toDomain(
                    onFetchNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->

                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            nonHumanAnimalId = nonHumanAnimalId,
                            caregiverId = caregiverId
                        ).map { uiState ->
                            if (uiState is UiState.Success) {
                                uiState.data
                            } else {
                                null
                            }
                        }.firstOrNull()
                    }
                )
            }
        }
}
