package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository

class DeleteMyChatFromLocalRepository(private val localChatRepository: LocalChatRepository) {

    suspend operator fun invoke(
        id: String,
        onDeleteChat: suspend (rowsDeleted: Int) -> Unit
    ) {
        localChatRepository.deleteChat(id, onDeleteChat)
    }
}
