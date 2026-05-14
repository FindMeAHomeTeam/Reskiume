package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository

class IsNonHumanAnimalInChatInLocalRepository(private val localChatRepository: LocalChatRepository) {

    suspend operator fun invoke(nonHumanAnimalId: String) =
        localChatRepository.isNonHumanAnimalInChat(nonHumanAnimalId)
}
