package com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeFireStoreRemoteChatRepository(
    private val remoteChatList: MutableList<RemoteChat> = mutableListOf(),
    private val remoteChatMessageList: MutableList<RemoteChatMessage> = mutableListOf()
) : FireStoreRemoteChatRepository {

    override fun insertRemoteChat(remoteChat: RemoteChat): Flow<DatabaseResult> {
        val chat = remoteChatList.firstOrNull { it.id == remoteChat.id }

        return if (chat == null) {
            remoteChatList.add(remoteChat)
            flowOf(DatabaseResult.Success)
        } else {
            flowOf(DatabaseResult.Error("error adding a chat"))
        }
    }

    override fun insertRemoteChatMessage(remoteChatMessage: RemoteChatMessage): Flow<DatabaseResult> {
        val chatMessage =
            remoteChatMessageList.firstOrNull { it.id == remoteChatMessage.id }

        return if (chatMessage == null) {
            remoteChatMessageList.add(remoteChatMessage)
            flowOf(DatabaseResult.Success)
        } else {
            flowOf(DatabaseResult.Error("error adding a chat"))
        }
    }

    override fun modifyRemoteChat(remoteChat: RemoteChat): Flow<DatabaseResult> {
        val chat =
            remoteChatList.firstOrNull { it.id == remoteChat.id }

        return if (chat == null) {
            flowOf(DatabaseResult.Error("error modifying a chat"))
        } else {
            remoteChatList[remoteChatList.indexOf(chat)] = remoteChat
            flowOf(DatabaseResult.Success)
        }
    }

    override fun modifyOnlyActivistsInRemoteChat(
        chatId: String,
        activistId: String,
        shouldAdd: Boolean
    ): Flow<DatabaseResult> {
        val chat =
            remoteChatList.firstOrNull { it.id == chatId }

        return if (chat == null) {
            flowOf(DatabaseResult.Error("error modifying a chat"))
        } else {
            if (shouldAdd) {
                remoteChatList[remoteChatList.indexOf(chat)] =
                    chat.copy(allActivistsInfo = chat.allActivistsInfo!! + activistId)
            } else {
                remoteChatList[remoteChatList.indexOf(chat)] =
                    chat.copy(allActivistsInfo = chat.allActivistsInfo!!.filter { it != activistId })
            }
            flowOf(DatabaseResult.Success)
        }
    }

    override fun deleteRemoteChat(
        uid: String,
        remoteChatId: String
    ): Flow<DatabaseResult> {

        val remoteChat =
            remoteChatList.firstOrNull { it.id == remoteChatId }

        return if (remoteChat == null) {
            flowOf(DatabaseResult.Error("error deleting a chat"))
        } else {
            remoteChatList.remove(remoteChat)
            flowOf(DatabaseResult.Success)
        }
    }

    override fun deleteAllMyRemoteChats(uid: String): Flow<DatabaseResult> {

        val chatList = remoteChatList.filter { it.chatHolderId == uid }

        return if (chatList.isEmpty()) {
            flowOf(DatabaseResult.Error("error deleting all chats"))
        } else {
            remoteChatList.removeAll(chatList)
            flowOf(DatabaseResult.Success)
        }
    }

    override fun getRemoteChat(id: String): Flow<RemoteChat?> =
        flowOf(remoteChatList.firstOrNull { it.id == id })

    override fun getRemoteChatMessages(
        chatId: String,
        lastTimestamp: Long
    ): Flow<List<RemoteChatMessage>> =
        flowOf(remoteChatMessageList.filter { it.chatId == chatId && it.timestamp!! >= lastTimestamp })

    override fun getAllMyRemoteChats(
        uid: String,
        lastChatTimestamp: Long
    ): Flow<List<RemoteChat>> =
        flowOf(remoteChatList.filter {
            (it.chatHolderId == uid || it.allActivistsInfo!!.contains(uid)) && it.timestamp!! >= lastChatTimestamp
        })
}
