package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository

class DeleteAllMyChatsFromLocalRepository(private val localChatRepository: LocalChatRepository) {

    suspend operator fun invoke(
        uid: String,
        onDeleteAllMyChats: (rowsDeleted: Int) -> Unit
    ) {
        localChatRepository.deleteAllMyChats(uid, onDeleteAllMyChats)
    }
}
