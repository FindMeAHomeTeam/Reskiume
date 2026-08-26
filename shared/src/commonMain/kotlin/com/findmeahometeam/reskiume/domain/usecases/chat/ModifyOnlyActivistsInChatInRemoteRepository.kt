package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow

class ModifyOnlyActivistsInChatInRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(
        chatId: String,
        activistId: String,
        shouldAdd: Boolean
    ): Flow<DatabaseResult> =
        fireStoreRemoteChatRepository.modifyOnlyActivistsInRemoteChat(
            chatId,
            activistId,
            shouldAdd
        )
}
