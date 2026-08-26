package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage

interface FireStoreRemoteChatRepositoryForIosDelegate {

    suspend fun insertRemoteChat(
        remoteChat: RemoteChat,
        onInsertRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun insertRemoteChatMessage(
        remoteChatMessage: RemoteChatMessage,
        onInsertRemoteChatMessage: (result: DatabaseResult) -> Unit
    )

    suspend fun modifyRemoteChat(
        remoteChat: RemoteChat,
        onModifyRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun modifyOnlyActivistsInRemoteChat(
        chatId: String,
        activistId: String,
        shouldAdd: Boolean,
        onModifyOnlyActivistsInRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteRemoteChat(
        uid: String,
        remoteChatId: String,
        onDeleteRemoteChat: (result: DatabaseResult) -> Unit
    )

    suspend fun deleteAllMyRemoteChats(
        uid: String,
        onDeleteAllMyRemoteChats: (result: DatabaseResult) -> Unit
    )
}
