package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat

interface FireStoreRemoteChatRepositoryForIosDelegate {

    suspend fun insertRemoteChat(
        remoteChat: RemoteChat,
        onInsertRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun modifyRemoteChat(
        remoteChat: RemoteChat,
        onModifyRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteRemoteChat(
        id: String,
        onDeleteRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteAllMyRemoteChats(
        uid: String,
        onDeleteAllMyRemoteChats: (result: DatabaseResult) -> Unit
    )
}
