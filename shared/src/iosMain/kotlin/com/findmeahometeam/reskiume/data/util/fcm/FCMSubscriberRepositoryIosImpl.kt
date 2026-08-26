package com.findmeahometeam.reskiume.data.util.fcm

import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FCMSubscriberRepositoryIosImpl(
    private val fCMSubscriberRepositoryForIosDelegateWrapper: FCMSubscriberRepositoryForIosDelegateWrapper
) : FCMSubscriberRepository {

    private suspend fun initialCheck(
        onSuccess: suspend (FCMSubscriberRepositoryForIosDelegate) -> Unit,
        onFailure: suspend () -> Unit = {}
    ) {
        val value =
            fCMSubscriberRepositoryForIosDelegateWrapper.fCMSubscriberRepositoryForIosDelegateState.value
        if (value != null) {
            onSuccess(value)
        } else {
            onFailure()
        }
    }

    override suspend fun subscribeToTopic(
        topic: String
    ): Flow<Boolean> = flow {
        initialCheck(
            onSuccess = {
                it.subscribeToTopic(topic)
                emit(true)
            },
            onFailure = {
                emit(false)
            }
        )
    }

    override suspend fun subscribeToAllTopics(allTopics: List<Subscription>): Flow<Boolean> =
        flow {
            initialCheck(
                onSuccess = {
                    it.subscribeToAllTopics(allTopics)
                    emit(true)
                },
                onFailure = {
                    emit(false)
                }
            )
        }

    override suspend fun unsubscribeFromTopic(
        topic: String
    ): Flow<Boolean> = flow {
        initialCheck(
            onSuccess = {
                it.unsubscribeFromTopic(topic)
                emit(true)
            },
            onFailure = {
                emit(false)
            }
        )
    }

    override suspend fun unsubscribeFromAllTopics(allTopics: List<Subscription>): Flow<Boolean> =
        flow {
            initialCheck(
                onSuccess = {
                    it.unsubscribeFromAllTopics(allTopics)
                    emit(true)
                },
                onFailure = {
                    emit(false)
                }
            )
        }
}
