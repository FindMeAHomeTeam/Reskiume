package com.findmeahometeam.reskiume.usecases.user

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertUserInLocalDataSourceTest {

    private val onInsertUserInLocal = Capture.slot<(suspend (rowId: Long) -> Unit)>()

    val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(user.image) } returns user.image

        every { getFileNameFromLocalImagePath(user.image) } returns user.image
    }

    val localUserRepository: LocalUserRepository = mock {
        everySuspend {
            insertUser(
                user.toEntity(),
                capture(onInsertUserInLocal)
            )
        } calls {
            onInsertUserInLocal.get().invoke(1L)
        }

        everySuspend {
            insertSubscription(
                user.subscriptions[0].toEntity(),
                any()
            )
        } returns Unit
    }

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val fCMSubscriberRepository: FCMSubscriberRepository = mock {
        everySuspend { subscribeToTopic(user.subscriptions[0].topic) } returns flowOf(true)
    }

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val insertUserInLocalDataSource =
        InsertUserInLocalDataSource(
            authRepository,
            manageImagePath,
            localUserRepository,
            fCMSubscriberRepository,
            log
        )

    @Test
    fun `given a user_when the app saves it in the local data source_then it calls to getFileNameFromLocalImagePath insertUser and insertSubscription`() =
        runTest {
            insertUserInLocalDataSource(user) {}
            verifySuspend {
                manageImagePath.getFileNameFromLocalImagePath(user.image)
                localUserRepository.insertUser(user.toEntity(), any())
                localUserRepository.insertSubscription(user.subscriptions[0].toEntity(), any())
            }
        }
}
