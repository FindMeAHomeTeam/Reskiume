package com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat

import com.findmeahometeam.reskiume.data.remote.response.chat.QueryChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

interface FireStoreRemoteChatFlowsRepositoryForIosDelegate {
    
    fun updateQueryChat(queryChat: QueryChat)
    
    @NativeCoroutines
    val queryChatFlow: Flow<QueryChat>
    
    fun updateRemoteChatListFlow(delegate: List<RemoteChat>)
    
    val remoteChatListFlow: Flow<List<RemoteChat>>
}
