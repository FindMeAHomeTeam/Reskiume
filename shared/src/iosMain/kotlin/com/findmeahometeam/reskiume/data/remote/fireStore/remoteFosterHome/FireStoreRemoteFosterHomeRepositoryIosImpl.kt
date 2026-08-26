package com.findmeahometeam.reskiume.data.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.QueryFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FireStoreRemoteFosterHomeRepositoryIosImpl(
    private val fireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper: FireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper,
    private val fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
) : FireStoreRemoteFosterHomeRepository {

    private suspend fun initialCheck(
        onSuccess: suspend (FireStoreRemoteFosterHomeRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            fireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper.fireStoreRemoteFosterHomeRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override suspend fun insertRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onInsertRemoteFosterHome: (DatabaseResult) -> Unit
    ) {
        if (remoteFosterHome.ownerId.isNullOrBlank()) return onInsertRemoteFosterHome(DatabaseResult.Error())

        initialCheck(
            onSuccess = {
                it.insertRemoteFosterHome(remoteFosterHome, onInsertRemoteFosterHome)
            },
            onFailure = {
                onInsertRemoteFosterHome(DatabaseResult.Error())
            }
        )
    }

    override suspend fun modifyRemoteFosterHome(
        remoteFosterHome: RemoteFosterHome,
        onModifyRemoteFosterHome: (DatabaseResult) -> Unit
    ) {
        if (remoteFosterHome.ownerId.isNullOrBlank()) return onModifyRemoteFosterHome(DatabaseResult.Error())

        initialCheck(
            onSuccess = {
                it.modifyRemoteFosterHome(remoteFosterHome, onModifyRemoteFosterHome)
            },
            onFailure = {
                onModifyRemoteFosterHome(DatabaseResult.Error())
            }
        )
    }

    override suspend fun deleteRemoteFosterHome(
        id: String,
        ownerId: String,
        onDeleteRemoteFosterHome: (DatabaseResult) -> Unit
    ) {
        if (id.isBlank() || ownerId.isBlank()) return onDeleteRemoteFosterHome(DatabaseResult.Error())

        val value =
            fireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper.fireStoreRemoteFosterHomeRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteRemoteFosterHome(id, ownerId, onDeleteRemoteFosterHome)
        } else {
            onDeleteRemoteFosterHome(DatabaseResult.Error())
        }
    }

    override suspend fun deleteAllMyRemoteFosterHomes(
        ownerId: String,
        onDeleteAllMyRemoteFosterHomes: (DatabaseResult) -> Unit
    ) {
        if (ownerId.isBlank()) return onDeleteAllMyRemoteFosterHomes(DatabaseResult.Error())

        val value =
            fireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper.fireStoreRemoteFosterHomeRepositoryForIosDelegateState.value
        if (value != null) {
            value.deleteAllMyRemoteFosterHomes(ownerId, onDeleteAllMyRemoteFosterHomes)
        } else {
            onDeleteAllMyRemoteFosterHomes(DatabaseResult.Error())
        }
    }

    override fun getRemoteFosterHome(id: String): Flow<RemoteFosterHome?> {

        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
            .updateQueryFosterHome(QueryFosterHome(id = id))
        return fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.remoteFosterHomeListFlow.map { it.firstOrNull() }
    }

    override fun getAllMyRemoteFosterHomes(ownerId: String): Flow<List<RemoteFosterHome>> {

        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
            .updateQueryFosterHome(QueryFosterHome(ownerId = ownerId))
        return fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.remoteFosterHomeListFlow
    }

    override fun getAllRemoteFosterHomesByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RemoteFosterHome?>> {

        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
            .updateQueryFosterHome(QueryFosterHome(country = country, city = city))
        return fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.remoteFosterHomeListFlow
    }

    override fun getAllRemoteFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RemoteFosterHome?>> {

        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
            .updateQueryFosterHome(
                QueryFosterHome(
                    activistLongitude = activistLongitude,
                    activistLatitude = activistLatitude,
                    rangeLongitude = rangeLongitude,
                    rangeLatitude = rangeLatitude
                )
            )
        return fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.remoteFosterHomeListFlow
    }
}
