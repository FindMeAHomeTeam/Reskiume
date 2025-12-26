package com.findmeahometeam.reskiume.usecases.localCache

import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.localCache
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ModifyCacheInLocalRepositoryTest {

    val localCacheRepository: LocalCacheRepository = mock {
        everySuspend {
            getLocalCacheEntity(
                localCache.cachedObjectId,
                localCache.section
            )
        } returns localCache.toEntity()

        everySuspend {
            modifyLocalCacheEntity(
                localCache.toEntity(),
                any()
            )
        } returns Unit
    }

    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository =
        ModifyCacheInLocalRepository(localCacheRepository)

    @Test
    fun `given a local cache_when the app modifies data in it_then getLocalCacheEntity and modifyLocalCacheEntity are called`() =
        runTest {
            modifyCacheInLocalRepository(localCache, {})
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(localCache.cachedObjectId, localCache.section)
                localCacheRepository.modifyLocalCacheEntity(localCache.toEntity(), any())
            }
        }
}
