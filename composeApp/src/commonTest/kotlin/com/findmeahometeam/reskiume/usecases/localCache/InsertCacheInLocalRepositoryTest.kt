package com.findmeahometeam.reskiume.usecases.localCache

import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.localCache
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertCacheInLocalRepositoryTest {

    val localCacheRepository: LocalCacheRepository = mock {
        everySuspend {
            insertLocalCacheEntity(
                localCache.toEntity(),
                any()
            )
        } returns Unit
    }

    private val insertCacheInLocalRepository: InsertCacheInLocalRepository =
        InsertCacheInLocalRepository(localCacheRepository)

    @Test
    fun `given a local cache_when the app inserts data into it_then insertLocalCacheEntity is called`() =
        runTest {
            insertCacheInLocalRepository(localCache, {})
            verifySuspend {
                localCacheRepository.insertLocalCacheEntity(localCache.toEntity(), any())
            }
        }
}
