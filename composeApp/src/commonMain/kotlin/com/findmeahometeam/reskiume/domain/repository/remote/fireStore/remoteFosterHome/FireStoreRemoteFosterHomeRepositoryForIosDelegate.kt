package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome

interface FireStoreRemoteFosterHomeRepositoryForIosDelegate {
    
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
}
