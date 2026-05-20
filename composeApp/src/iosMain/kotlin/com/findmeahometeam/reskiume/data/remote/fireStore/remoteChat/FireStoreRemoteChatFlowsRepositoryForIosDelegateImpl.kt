package com.findmeahometeam.reskiume.data.remote.fireStore.remoteChat

import com.findmeahometeam.reskiume.data.remote.response.chat.QueryChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatFlowsRepositoryForIosDelegate
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FireStoreRemoteChatFlowsRepositoryForIosDelegateImpl :
    FireStoreRemoteChatFlowsRepositoryForIosDelegate {

    private val _queryChatState: MutableSharedFlow<QueryChat> =
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun updateQueryChat(queryChat: QueryChat) {
        _queryChatState.tryEmit(queryChat)
    }

    override val queryChatFlow: Flow<QueryChat> =
        _queryChatState.asSharedFlow()

    private val _remoteChatListState: MutableSharedFlow<List<RemoteChat>> =
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun updateRemoteChatListFlow(delegate: List<RemoteChat>) {
        _remoteChatListState.tryEmit(delegate)
    }

    override val remoteChatListFlow: Flow<List<RemoteChat>> =
        _remoteChatListState.asSharedFlow()

    private val _remoteChatMessageListState: MutableSharedFlow<List<RemoteChatMessage>> =
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun updateRemoteChatMessageListFlow(delegate: List<RemoteChatMessage>) {
        _remoteChatMessageListState.tryEmit(delegate)
    }

    override val remoteChatMessageListFlow: Flow<List<RemoteChatMessage>> =
        _remoteChatMessageListState.asSharedFlow()
}
