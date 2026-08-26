package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.data.remote.response.fosterHome.QueryFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate {
    
    fun updateQueryFosterHome(queryFosterHome: QueryFosterHome)
    
    @NativeCoroutines
    val queryFosterHomeFlow: Flow<QueryFosterHome>
    
    fun updateRemoteFosterHomeListFlow(delegate: List<RemoteFosterHome>)
    
    val remoteFosterHomeListFlow: Flow<List<RemoteFosterHome>>
}
