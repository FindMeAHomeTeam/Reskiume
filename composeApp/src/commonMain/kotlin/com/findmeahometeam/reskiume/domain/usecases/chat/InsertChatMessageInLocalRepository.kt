package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository

class InsertChatMessageInLocalRepository(
    private val localChatRepository: LocalChatRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        chatMessage: ChatMessage,
        onInsertChatMessage: suspend (isSuccess: Boolean) -> Unit
    ) {
        localChatRepository.insertChatMessageEntity(
            chatMessage.toEntity(),
            onInsertChatMessageEntity = { rowId ->
                if (rowId > 0) {
                    log.d(
                        "InsertChatMessageInLocalRepository",
                        "insertChatMessage: inserted the chat message ${chatMessage.id} in the chat ${chatMessage.chatId} in the local data source"
                    )
                } else {
                    log.e(
                        "InsertChatMessageInLocalRepository",
                        "insertChatMessage: failed to insert the chat message ${chatMessage.id} in the chat ${chatMessage.chatId} in the local data source"
                    )
                }
                onInsertChatMessage(rowId > 0)
            }
        )
    }
}
