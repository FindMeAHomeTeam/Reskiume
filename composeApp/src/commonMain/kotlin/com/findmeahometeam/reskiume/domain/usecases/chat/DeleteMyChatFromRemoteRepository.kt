package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import kotlinx.coroutines.flow.Flow

class DeleteMyChatFromRemoteRepository(private val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository) {

    operator fun invoke(
        uid: String,
        remoteChatId: String
    ): Flow<DatabaseResult> = fireStoreRemoteChatRepository.deleteRemoteChat(
        uid,
        remoteChatId
    )
}
