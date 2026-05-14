package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllChatMessagesFromLocalRepository(private val localChatRepository: LocalChatRepository) {

    operator fun invoke(chatId: String): Flow<List<ChatMessage>> =
        localChatRepository.getAllMyChatMessages(chatId = chatId)
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }
}
