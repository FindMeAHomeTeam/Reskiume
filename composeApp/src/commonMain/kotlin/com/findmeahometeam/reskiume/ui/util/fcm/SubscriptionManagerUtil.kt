package com.findmeahometeam.reskiume.ui.util.fcm

import com.findmeahometeam.reskiume.domain.model.user.User
import kotlinx.coroutines.CoroutineScope

interface SubscriptionManagerUtil {
    suspend fun subscribeToAllTopicsAfterLogin(
        user: User
    )

    suspend fun subscribeToTopic(
        user: User,
        topic: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    )

    suspend fun unsubscribeFromAllTopicsAfterLogOut(user: User)

    suspend fun unsubscribeFromTopic(
        user: User,
        topicToUnsubscribe: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    )
}
