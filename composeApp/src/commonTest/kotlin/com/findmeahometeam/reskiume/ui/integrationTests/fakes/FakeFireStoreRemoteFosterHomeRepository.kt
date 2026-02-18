package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFireStoreRemoteFosterHomeRepository(
    private val remoteFosterHomeList: MutableList<RemoteFosterHome> = mutableListOf()
) : FireStoreRemoteFosterHomeRepository {

    override suspend fun insertRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onInsertRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val fosterHome =
            remoteFosterHomeList.firstOrNull { it.id == remoteFosterHome.id }
        if (fosterHome == null) {
            remoteFosterHomeList.add(remoteFosterHome)
            onInsertRemoteFosterHome(DatabaseResult.Success)
        } else {
            onInsertRemoteFosterHome(DatabaseResult.Error("error adding a foster home"))
        }
    }

    override suspend fun modifyRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onModifyRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val fosterHome =
            remoteFosterHomeList.firstOrNull { it.id == remoteFosterHome.id }
        if (fosterHome == null) {
            onModifyRemoteFosterHome(DatabaseResult.Error("error modifying a foster home"))
        } else {
            remoteFosterHomeList[remoteFosterHomeList.indexOf(fosterHome)] = remoteFosterHome
            onModifyRemoteFosterHome(DatabaseResult.Success)
        }
    }

    override suspend fun deleteRemoteFosterHome(
        id: String,
        ownerId: String,
        onDeleteRemoteFosterHome: (result: DatabaseResult) -> Unit
    ) {
        val fosterHome =
            remoteFosterHomeList.firstOrNull { it.id == id && it.ownerId == ownerId }
        if (fosterHome == null) {
            onDeleteRemoteFosterHome(DatabaseResult.Error("error deleting a foster home"))
        } else {
            remoteFosterHomeList.remove(fosterHome)
            onDeleteRemoteFosterHome(DatabaseResult.Success)
        }
    }

    override suspend fun deleteAllMyRemoteFosterHomes(
        ownerId: String,
        onDeleteAllMyRemoteFosterHomes: (result: DatabaseResult) -> Unit
    ) {
        val fosterHomeList = remoteFosterHomeList.filter { it.ownerId == ownerId }
        if (fosterHomeList.isEmpty()) {
            onDeleteAllMyRemoteFosterHomes(DatabaseResult.Error("error deleting all foster homes"))
        } else {
            remoteFosterHomeList.removeAll(fosterHomeList)
            onDeleteAllMyRemoteFosterHomes(DatabaseResult.Success)
        }
    }

    override fun getRemoteFosterHome(id: String): Flow<RemoteFosterHome?> =
        flowOf(remoteFosterHomeList.firstOrNull { it.id == id })

    override fun getAllMyRemoteFosterHomes(ownerId: String): Flow<List<RemoteFosterHome>> =
        flowOf(remoteFosterHomeList.filter { it.ownerId == ownerId })

    override fun getAllRemoteFosterHomesByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RemoteFosterHome?>> =
        flowOf(remoteFosterHomeList.filter { it.country == country && it.city == city })

    override fun getAllRemoteFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteFosterHome?>> =
        flowOf(
            remoteFosterHomeList.filter {
                it.longitude!! >= activistLongitude - rangeLongitude
                        && it.longitude <= activistLongitude + rangeLongitude
                        && it.latitude!! >= activistLatitude - rangeLatitude
                        && it.latitude <= activistLatitude + rangeLatitude
            }
        )
}
