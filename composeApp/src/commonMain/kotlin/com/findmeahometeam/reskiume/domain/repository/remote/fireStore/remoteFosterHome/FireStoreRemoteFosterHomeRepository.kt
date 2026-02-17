package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import kotlinx.coroutines.flow.Flow

interface FireStoreRemoteFosterHomeRepository {

    suspend fun insertRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onInsertRemoteFosterHome: (result: DatabaseResult) -> Unit
    )

    suspend fun modifyRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onModifyRemoteFosterHome: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteRemoteFosterHome(
        id: String,
        ownerId: String,
        onDeleteRemoteFosterHome: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteAllMyRemoteFosterHomes(
        ownerId: String,
        onDeleteAllMyRemoteFosterHomes: (result: DatabaseResult) -> Unit
    )

    fun getRemoteFosterHome(id: String): Flow<RemoteFosterHome?>

    fun getAllMyRemoteFosterHomes(ownerId: String): Flow<List<RemoteFosterHome?>>

    fun getAllRemoteFosterHomesByCountryAndCity(country: String, city: String): Flow<List<RemoteFosterHome?>>

    fun getAllRemoteFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteFosterHome?>>
}
