package com.findmeahometeam.reskiume.ui.chats.checkAllMyChats

import com.findmeahometeam.reskiume.domain.model.chat.Chat

interface ManageChatUtil {

    suspend fun insertChatInLocalRepo(
        chat: Chat,
        myUid: String
    )

    suspend fun modifyChatInLocalRepo(
        updatedChat: Chat,
        previousChat: Chat,
        myUid: String
    )
}
