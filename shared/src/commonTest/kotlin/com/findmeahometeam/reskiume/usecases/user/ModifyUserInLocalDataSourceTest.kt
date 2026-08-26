package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyUserInLocalDataSourceTest {

    private val onModifyUserInLocal = Capture.slot<(suspend (rowsUpdated: Int) -> Unit)>()

    private val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(user.image) } returns user.image

        every { getFileNameFromLocalImagePath(user.image) } returns user.image
    }

    private val fCMSubscriberRepository: FCMSubscriberRepository = mock {
        everySuspend { subscribeToTopic("newTopic") } returns flowOf(true)
        everySuspend { unsubscribeFromTopic(user.subscriptions[0].topic) } returns flowOf(true)
    }

    private val localUserRepository: LocalUserRepository = mock {
        everySuspend {
            modifyUser(
                any(),
                capture(onModifyUserInLocal)
            )
        } calls { onModifyUserInLocal.get().invoke(1) }

        every { getUser(user.uid) } returns flowOf(userWithAllSubscriptionData)

        everySuspend {
            insertSubscription(
                user.subscriptions[0].copy(
                    topic = "newTopic"
                ).toEntity(),
                any()
            )
        } returns Unit

        everySuspend {
            deleteSubscription(
                user.subscriptions[0].subscriptionId,
                any()
            )
        } returns Unit
    }

    private val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val modifyUserInLocalDataSource =
        ModifyUserInLocalDataSource(
            manageImagePath,
            fCMSubscriberRepository,
            localUserRepository,
            authRepository,
            log
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my user_when the app updates my user replacing my subscription in the local data source_then it calls to getFileNameFromLocalImagePath and some localUserRepository methods`() =
        runTest {
            modifyUserInLocalDataSource(
                user.copy(
                    subscriptions = listOf(
                        user.subscriptions[0].copy(
                            topic = "newTopic"
                        )
                    )
                )
            ) {}

            runCurrent()

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(user.image)
                localUserRepository.modifyUser(user.toEntity(), any())
                localUserRepository.insertSubscription(
                    user.subscriptions[0].copy(
                        topic = "newTopic"
                    ).toEntity(),
                    any()
                )
                localUserRepository.deleteSubscription(
                    user.subscriptions[0].subscriptionId,
                    any()
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given other user_when the app updates other user in the local data source_then it only calls to getFileNameFromLocalImagePath and modifyUser methods without updating user the subscription`() =
        runTest {
            modifyUserInLocalDataSource(
                user.copy(
                    uid = "otherUid",
                    subscriptions = listOf(
                        user.subscriptions[0].copy(
                            topic = "newTopic"
                        )
                    )
                )
            ) {}

            runCurrent()

            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(user.image)
                localUserRepository.modifyUser(user.copy(uid = "otherUid").toEntity(), any())
            }
            verifySuspend(exactly(0)) {
                localUserRepository.insertSubscription(
                    user.subscriptions[0].copy(
                        topic = "newTopic"
                    ).toEntity(),
                    any()
                )
                localUserRepository.deleteSubscription(
                    user.subscriptions[0].subscriptionId,
                    any()
                )
            }
        }
}
