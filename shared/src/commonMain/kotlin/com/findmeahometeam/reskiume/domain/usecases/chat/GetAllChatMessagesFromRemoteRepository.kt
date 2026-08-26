package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllChatMessagesFromRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(
        chatId: String,
        lastTimestamp: Long = 0L
    ): Flow<List<ChatMessage>> =
        fireStoreRemoteChatRepository.getRemoteChatMessages(
            chatId = chatId,
            lastTimestamp = lastTimestamp
        ).map { list ->
            list.map {
                it.toDomain()
            }
        }
}
