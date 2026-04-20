package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow

class InsertChatInRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(chat: Chat): Flow<DatabaseResult> =
        fireStoreRemoteChatRepository.insertRemoteChat(
            chat.toRemoteChat(),
            chat.toRemoteChatMessageList()
        )
}
