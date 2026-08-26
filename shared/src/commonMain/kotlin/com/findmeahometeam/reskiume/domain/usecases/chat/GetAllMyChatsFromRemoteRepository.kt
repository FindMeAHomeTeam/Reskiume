package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetAllMyChatsFromRemoteRepository(
    private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository,
    private val localChatRepository: LocalChatRepository
) {
    operator fun invoke(
        uid: String,
        lastChatTimestamp: Long = 0L
    ): Flow<List<Chat>> =
        fireStoreRemoteChatRepository.getAllMyRemoteChats(
            uid,
            lastChatTimestamp
        ).map { list ->
            list.map { remoteChat ->

                val allRemoteChatMessages = fireStoreRemoteChatRepository.getRemoteChatMessages(
                    chatId = remoteChat.id!!,
                    lastTimestamp = localChatRepository.getAllMyChatMessages(remoteChat.id).first()
                        .let {
                            if (it.isEmpty()) {
                                0L
                            } else {
                                it.last().timestamp
                            }
                        }
                ).first()

                remoteChat.toDomain(
                    myUid = uid,
                    allChatMessages = allRemoteChatMessages
                )
            }
        }
}
