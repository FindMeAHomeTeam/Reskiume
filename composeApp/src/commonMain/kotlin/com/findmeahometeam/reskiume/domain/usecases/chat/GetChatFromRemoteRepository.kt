package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull

class GetChatFromRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(
        id: String,
        myUid: String,
        lastTimestamp: Long = 0L
    ): Flow<Chat> =
        fireStoreRemoteChatRepository.getRemoteChat(id)
            .mapNotNull { remoteChat ->

                if (remoteChat == null) {
                    return@mapNotNull null
                }

                val allChatMessages = fireStoreRemoteChatRepository.getRemoteChatMessages(
                    chatId = remoteChat.id!!,
                    lastTimestamp = lastTimestamp
                ).first()

                remoteChat.toDomain(
                    myUid = myUid,
                    allChatMessages = allChatMessages
                )
            }
}
