package com.findmeahometeam.reskiume.domain.usecases.rescueEvent

import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllRescueEventsByLocationFromRemoteRepository(private val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository) {

    operator fun invoke(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RescueEvent>> =
        fireStoreRemoteRescueEventRepository.getAllRemoteRescueEventsByLocation(
            activistLongitude,
            activistLatitude,
            rangeLongitude,
            rangeLatitude
        ).map { list: List<RemoteRescueEvent?> ->
            list.mapNotNull {
                it?.toDomain()
            }
        }
}
