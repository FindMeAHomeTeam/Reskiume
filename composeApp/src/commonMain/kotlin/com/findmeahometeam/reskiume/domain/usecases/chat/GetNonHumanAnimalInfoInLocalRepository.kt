package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import kotlinx.coroutines.flow.map

class GetNonHumanAnimalInfoInLocalRepository(private val localChatRepository: LocalChatRepository) {

    operator fun invoke(nonHumanAnimalId: String) =
        localChatRepository.getNonHumanAnimalInfo(nonHumanAnimalId).map { it?.toDomain() }
}
