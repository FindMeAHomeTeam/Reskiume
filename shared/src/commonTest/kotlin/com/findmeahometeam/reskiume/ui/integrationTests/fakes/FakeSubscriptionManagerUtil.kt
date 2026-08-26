package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import kotlinx.coroutines.CoroutineScope

class FakeSubscriptionManagerUtil: SubscriptionManagerUtil {

    override suspend fun subscribeToAllTopicsAfterLogin(user: User) {}

    override suspend fun subscribeToTopic(
        user: User,
        topic: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        onComplete()
    }

    override suspend fun unsubscribeFromAllTopicsAfterLogOut(user: User) {}

    override suspend fun unsubscribeFromTopic(
        user: User,
        topicToUnsubscribe: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        onComplete()
    }
}
