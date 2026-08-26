package com.findmeahometeam.reskiume.data.remote.fireStore.remoteChat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.QueryChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatFlowsRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepositoryForIosDelegateWrapper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class FireStoreRemoteChatRepositoryIosImpl(
    private val fireStoreRemoteChatRepositoryForIosDelegateWrapper: FireStoreRemoteChatRepositoryForIosDelegateWrapper,
    private val fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate
) : FireStoreRemoteChatRepository {

    private suspend fun initialCheck(
        onSuccess: suspend (FireStoreRemoteChatRepositoryForIosDelegate) -> Unit,
        onFailure: () -> Unit
    ) {
        val value =
            fireStoreRemoteChatRepositoryForIosDelegateWrapper.fireStoreRemoteChatRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override fun insertRemoteChat(
        remoteChat: RemoteChat
    ): Flow<DatabaseResult> = callbackFlow {
        initialCheck(
            onSuccess = {
                it.insertRemoteChat(remoteChat) { databaseResult ->
                    trySend(databaseResult)
                    close()
                }
            },
            onFailure = {
                trySend(DatabaseResult.Error())
                close()
            }
        )
        awaitClose()
    }

    override fun insertRemoteChatMessage(remoteChatMessage: RemoteChatMessage): Flow<DatabaseResult> =
        callbackFlow {
            initialCheck(
                onSuccess = {
                    it.insertRemoteChatMessage(remoteChatMessage) { databaseResult ->
                        trySend(databaseResult)
                        close()
                    }
                },
                onFailure = {
                    trySend(DatabaseResult.Error())
                    close()
                }
            )
            awaitClose()
        }

    override fun modifyRemoteChat(remoteChat: RemoteChat): Flow<DatabaseResult> = callbackFlow {
        initialCheck(
            onSuccess = {
                it.modifyRemoteChat(remoteChat) { databaseResult ->
                    trySend(databaseResult)
                    close()
                }
            },
            onFailure = {
                trySend(DatabaseResult.Error())
                close()
            }
        )
        awaitClose()
    }

    override fun modifyOnlyActivistsInRemoteChat(
        chatId: String,
        activistId: String,
        shouldAdd: Boolean
    ): Flow<DatabaseResult> = callbackFlow {
        initialCheck(
            onSuccess = {
                it.modifyOnlyActivistsInRemoteChat(
                    chatId,
                    activistId,
                    shouldAdd
                ) { databaseResult ->

                    trySend(databaseResult)
                    close()
                }
            },
            onFailure = {
                trySend(DatabaseResult.Error())
                close()
            }
        )
        awaitClose()
    }

    override fun deleteRemoteChat(
        uid: String,
        remoteChatId: String
    ): Flow<DatabaseResult> = callbackFlow {
        initialCheck(
            onSuccess = {
                it.deleteRemoteChat(
                    uid,
                    remoteChatId
                ) { databaseResult ->

                    trySend(databaseResult)
                    close()
                }
            },
            onFailure = {
                trySend(DatabaseResult.Error())
                close()
            }
        )
        awaitClose()
    }

    override fun deleteAllMyRemoteChats(uid: String): Flow<DatabaseResult> = callbackFlow {
        initialCheck(
            onSuccess = {
                it.deleteAllMyRemoteChats(uid) { databaseResult ->
                    trySend(databaseResult)
                    close()
                }
            },
            onFailure = {
                trySend(DatabaseResult.Error())
                close()
            }
        )
        awaitClose()
    }

    override fun getRemoteChat(id: String): Flow<RemoteChat?> {
        fireStoreRemoteChatFlowsRepositoryForIosDelegate
            .updateQueryChat(QueryChat(id = id))
        return fireStoreRemoteChatFlowsRepositoryForIosDelegate.remoteChatListFlow.map { it.firstOrNull() }
    }

    override fun getRemoteChatMessages(
        chatId: String,
        lastTimestamp: Long
    ): Flow<List<RemoteChatMessage>> {

        fireStoreRemoteChatFlowsRepositoryForIosDelegate
            .updateQueryChat(
                QueryChat(
                    id = chatId,
                    lastMessageTimestamp = lastTimestamp
                )
            )
        return fireStoreRemoteChatFlowsRepositoryForIosDelegate.remoteChatMessageListFlow
    }

    override fun getAllMyRemoteChats(
        uid: String,
        lastChatTimestamp: Long
    ): Flow<List<RemoteChat>> {
        fireStoreRemoteChatFlowsRepositoryForIosDelegate
            .updateQueryChat(
                QueryChat(
                    uid = uid,
                    lastChatTimestamp = lastChatTimestamp
                )
            )
        return fireStoreRemoteChatFlowsRepositoryForIosDelegate.remoteChatListFlow
    }
}
