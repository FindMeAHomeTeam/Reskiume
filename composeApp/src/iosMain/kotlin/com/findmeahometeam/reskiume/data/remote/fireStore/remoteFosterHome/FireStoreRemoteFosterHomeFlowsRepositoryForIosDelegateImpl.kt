package com.findmeahometeam.reskiume.data.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.data.remote.response.fosterHome.QueryFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegateImpl :
    FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate {

    private val _queryFosterHomeState: MutableSharedFlow<QueryFosterHome> =
        MutableSharedFlow(extraBufferCapacity = 1)

    override fun updateQueryFosterHome(queryFosterHome: QueryFosterHome) {
        _queryFosterHomeState.tryEmit(queryFosterHome)
    }

    override val queryFosterHomeFlow: Flow<QueryFosterHome> =
        _queryFosterHomeState.asSharedFlow()

    private val _remoteFosterHomeListState: MutableSharedFlow<List<RemoteFosterHome>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    override fun updateRemoteFosterHomeListFlow(delegate: List<RemoteFosterHome>) {
        _remoteFosterHomeListState.tryEmit(delegate)
    }

    override val remoteFosterHomeListFlow: Flow<List<RemoteFosterHome>> =
        _remoteFosterHomeListState.asSharedFlow()
}
