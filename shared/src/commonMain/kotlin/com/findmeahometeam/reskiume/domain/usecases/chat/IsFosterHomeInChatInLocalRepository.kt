package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository

class IsFosterHomeInChatInLocalRepository(private val localChatRepository: LocalChatRepository) {

    suspend operator fun invoke(fosterHomeId: String) =
        localChatRepository.isFosterHomeChat(fosterHomeId)
}
