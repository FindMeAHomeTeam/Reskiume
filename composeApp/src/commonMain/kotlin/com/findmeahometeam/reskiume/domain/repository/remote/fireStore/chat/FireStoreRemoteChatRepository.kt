package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage
import kotlinx.coroutines.flow.Flow

interface FireStoreRemoteChatRepository {

    fun insertRemoteChat(
        remoteChat: RemoteChat,
        remoteChatMessages: List<RemoteChatMessage>
    ): Flow<DatabaseResult>

    fun insertRemoteChatMessage(
        remoteChatMessage: RemoteChatMessage
    ): Flow<DatabaseResult>

    fun modifyRemoteChat(remoteChat: RemoteChat): Flow<DatabaseResult>

    fun deleteRemoteChat(
        uid: String,
        remoteChatId: String
    ): Flow<DatabaseResult>

    fun deleteAllMyRemoteChats(uid: String): Flow<DatabaseResult>

    fun getRemoteChat(id: String): Flow<RemoteChat?>

    fun getRemoteChatMessages(
        chatId: String,
        lastTimestamp: Long
    ): Flow<List<RemoteChatMessage>>

    fun getAllMyRemoteChats(uid: String): Flow<List<RemoteChat>>
}
