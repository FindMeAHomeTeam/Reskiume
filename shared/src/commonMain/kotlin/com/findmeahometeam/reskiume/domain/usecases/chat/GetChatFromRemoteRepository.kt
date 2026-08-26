package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetChatFromRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(
        id: String,
        myUid: String,
        lastTimestamp: Long = 0L
    ): Flow<Chat?> =
        fireStoreRemoteChatRepository.getRemoteChat(id)
            .map { remoteChat ->

                if (remoteChat == null) {
                    return@map null
                }

                remoteChat.toDomain(
                    myUid = myUid,
                    allChatMessages = emptyList()
                )
            }
}
