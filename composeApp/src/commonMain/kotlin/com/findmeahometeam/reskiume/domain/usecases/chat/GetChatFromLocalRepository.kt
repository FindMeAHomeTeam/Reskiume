package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetChatFromLocalRepository(private val localChatRepository: LocalChatRepository) {

    operator fun invoke(id: String): Flow<Chat?> =
        if (id.isEmpty()) {
            flowOf(null)
        } else {
            localChatRepository.getChat(id).map { chatEntityWithAllData: ChatEntityWithAllData? ->

                chatEntityWithAllData?.chatEntity?.toDomain(
                    allNonHumanAnimalsInfo = chatEntityWithAllData.allNonHumanAnimalsInfo.map { it.toDomain() },
                    allActivistsInfo = chatEntityWithAllData.allActivistsInfo.map { it.toDomain() },
                    allBlockedUsersInfo = chatEntityWithAllData.allBlockedUsersInfo.map { it.toDomain() },
                    allChatMessages = chatEntityWithAllData.allChatMessages.map { it.toDomain() }
                )
            }
        }
}
