package com.findmeahometeam.reskiume.usecases.localCache

import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteCacheFromLocalRepositoryTest {

    val localCacheRepository: LocalCacheRepository = mock {
        everySuspend {
            deleteLocalCacheEntity(
                user.uid,
                any()
            )
        } returns Unit
    }

    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository =
        DeleteCacheFromLocalRepository(localCacheRepository)

    @Test
    fun `given a local cache_when the app deletes it on account deletion_then deleteLocalCacheEntity is called`() =
        runTest {
            deleteCacheFromLocalRepository(user.uid, {})
            verifySuspend {
                localCacheRepository.deleteLocalCacheEntity(user.uid, any())
            }
        }
}
