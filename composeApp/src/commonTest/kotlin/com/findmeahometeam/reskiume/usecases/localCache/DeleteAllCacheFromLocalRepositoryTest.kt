package com.findmeahometeam.reskiume.usecases.localCache

import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteAllCacheFromLocalRepository
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteAllCacheFromLocalRepositoryTest {

    val localCacheRepository: LocalCacheRepository = mock {
        everySuspend {
            deleteAllLocalCacheEntity(
                user.uid,
                any()
            )
        } returns Unit
    }

    private val deleteAllCacheFromLocalRepository: DeleteAllCacheFromLocalRepository =
        DeleteAllCacheFromLocalRepository(localCacheRepository)

    @Test
    fun `given a local cache_when the app deletes it on account deletion_then deleteAllLocalCacheEntity is called`() =
        runTest {
            deleteAllCacheFromLocalRepository(user.uid, {})
            verifySuspend {
                localCacheRepository.deleteAllLocalCacheEntity(user.uid, any())
            }
        }
}
