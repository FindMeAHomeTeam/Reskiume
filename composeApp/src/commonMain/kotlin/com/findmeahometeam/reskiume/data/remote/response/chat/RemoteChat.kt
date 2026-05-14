package com.findmeahometeam.reskiume.data.remote.response.chat

import com.findmeahometeam.reskiume.domain.model.chat.ActivistInfo
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class RemoteChat(
    val id: String? = "",
    val fosterHomeId: String? = "",
    val rescueEventId: String? = "",
    val chatHolderId: String? = "",
    val allNonHumanAnimalsInfo: List<RemoteNonHumanAnimalInfo>? = emptyList(),
    val allActivistsInfo: List<String>? = emptyList(),
    val allBlockedUsersInfo: List<RemoteBlockedUserInfo>? = emptyList(),
    val acceptedFoster: Boolean? = false,
    val finished: Boolean? = false,
    val addReview: Boolean? = false,
    val timestamp: Long? = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "fosterHomeId" to fosterHomeId,
            "rescueEventId" to rescueEventId,
            "chatHolderId" to chatHolderId,
            "allNonHumanAnimalsInfo" to allNonHumanAnimalsInfo,
            "allActivistsInfo" to allActivistsInfo,
            "allBlockedUsersInfo" to allBlockedUsersInfo,
            "acceptedFoster" to acceptedFoster,
            "finished" to finished,
            "addReview" to addReview,
            "timestamp" to timestamp
        )
    }

    @OptIn(ExperimentalTime::class)
    fun toDomain(
        myUid: String,
        allChatMessages: List<RemoteChatMessage>
    ): Chat {
        return Chat(
            id = id ?: "",
            fosterHomeId = fosterHomeId ?: "",
            rescueEventId = rescueEventId ?: "",
            savedBy = myUid,
            chatHolderId = chatHolderId ?: "",
            allNonHumanAnimalsInfo = allNonHumanAnimalsInfo?.map { it.toDomain() } ?: emptyList(),
            allActivistsInfo = allActivistsInfo?.map {
                ActivistInfo(
                    id = Clock.System.now().epochSeconds.toString() + id,
                    chatId = id!!,
                    uid = it
                )
            } ?: emptyList(),
            allBlockedUsersInfo = allBlockedUsersInfo?.map { it.toDomain() } ?: emptyList(),
            allChatMessages = allChatMessages.map { it.toDomain() },
            myUserIsConnected = false,
            acceptedFoster = acceptedFoster ?: false,
            finished = finished ?: false,
            addReview = addReview ?: false,
            timestamp = timestamp ?: 0
        )
    }
}
