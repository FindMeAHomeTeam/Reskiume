package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow

class InsertChatMessageInRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(chatMessage: ChatMessage): Flow<DatabaseResult> =
        fireStoreRemoteChatRepository.insertRemoteChatMessage(chatMessage.toData())
}
