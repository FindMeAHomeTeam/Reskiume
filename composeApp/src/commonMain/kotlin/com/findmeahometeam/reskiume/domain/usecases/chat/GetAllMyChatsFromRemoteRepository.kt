package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetAllMyChatsFromRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(
        uid: String,
        lastTimestamp: Long = 0L
    ): Flow<List<Chat>> =
        fireStoreRemoteChatRepository.getAllMyRemoteChats(uid)
            .map { list ->

                list.map { remoteChat ->

                    val allChatMessages = fireStoreRemoteChatRepository.getRemoteChatMessages(
                        chatId = remoteChat.id!!,
                        lastTimestamp = lastTimestamp
                    ).first()

                    remoteChat.toDomain(
                        myUid = uid,
                        allChatMessages = allChatMessages
                    )
                }
            }
}
