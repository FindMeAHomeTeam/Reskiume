package com.findmeahometeam.reskiume.ui.chats.checkAllMyChats

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ManageChatUtilImpl(
    private val insertChatInLocalRepository: InsertChatInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyChatInLocalRepository: ModifyChatInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val log: Log
) : ManageChatUtil {

    @OptIn(ExperimentalTime::class)
    override suspend fun insertChatInLocalRepo(
        chat: Chat,
        myUid: String
    ) {
        insertChatInLocalRepository(chat) { isSuccess ->

            if (isSuccess) {
                log.d(
                    "ManageChatUtilImpl",
                    "insertChatInLocalRepo: Chat ${chat.id} added to local database"
                )
                insertCacheInLocalRepository(
                    LocalCache(
                        cachedObjectId = chat.id,
                        savedBy = myUid,
                        section = Section.CHATS,
                        timestamp = Clock.System.now().epochSeconds
                    )
                ) { rowId ->

                    if (rowId > 0) {
                        log.d(
                            "ManageChatUtilImpl",
                            "insertChatInLocalRepo: ${chat.id} added to local cache in section ${Section.CHATS}"
                        )
                    } else {
                        log.e(
                            "ManageChatUtilImpl",
                            "insertChatInLocalRepo: Error adding ${chat.id} to local cache in section ${Section.CHATS}"
                        )
                    }
                }
            } else {
                log.e(
                    "ManageChatUtilImpl",
                    "insertChatInLocalRepo: Error adding the chat ${chat.id} to local database"
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun modifyChatInLocalRepo(
        updatedChat: Chat,
        previousChat: Chat,
        myUid: String
    ) {
        modifyChatInLocalRepository(
            updatedChat,
            previousChat
        ) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "ManageChatUtilImpl",
                    "modifyChatInLocalRepo: Chat ${updatedChat.id} modified in local database"
                )
                modifyCacheInLocalRepository(
                    LocalCache(
                        cachedObjectId = updatedChat.id,
                        savedBy = myUid,
                        section = Section.CHATS,
                        timestamp = Clock.System.now().epochSeconds
                    )
                ) { rowsUpdated ->

                    if (rowsUpdated > 0) {
                        log.d(
                            "ManageChatUtilImpl",
                            "modifyChatInLocalRepo: ${updatedChat.id} updated in local cache in section ${Section.CHATS}"
                        )
                    } else {
                        log.e(
                            "ManageChatUtilImpl",
                            "modifyChatInLocalRepo: Error updating ${updatedChat.id} in local cache in section ${Section.CHATS}"
                        )
                    }
                }
            } else {
                log.e(
                    "ManageChatUtilImpl",
                    "modifyChatInLocalRepo: Error modifying the chat ${updatedChat.id} in local database"
                )
            }
        }
    }
}
