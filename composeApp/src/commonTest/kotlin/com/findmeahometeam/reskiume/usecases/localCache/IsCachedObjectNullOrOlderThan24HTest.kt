package com.findmeahometeam.reskiume.usecases.localCache

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.IsCachedObjectNullOrOlderThan24H
import com.findmeahometeam.reskiume.localCache
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IsCachedObjectNullOrOlderThan24HTest {

    private lateinit var localCacheRepository: LocalCacheRepository

    private fun getIsCachedObjectNullOrOlderThan24H(
        cacheArg: LocalCache = localCache,
        getLocalCacheEntityReturn: LocalCacheEntity? = localCache.toEntity()
    ): IsCachedObjectNullOrOlderThan24H {
        localCacheRepository = mock {
            everySuspend {
                getLocalCacheEntity(
                    cacheArg.uid,
                    cacheArg.section
                )
            } returns getLocalCacheEntityReturn
        }
        return IsCachedObjectNullOrOlderThan24H(localCacheRepository)
    }

    @Test
    fun `given a local cache_when the app checks if some data was set more than a day ago_then getLocalCacheEntity is called`() =
        runTest {
            val isCachedObjectNullOrOlderThan24H = getIsCachedObjectNullOrOlderThan24H()
            isCachedObjectNullOrOlderThan24H(localCache.uid, localCache.section)
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(localCache.uid, localCache.section)
            }
        }

    @Test
    fun `given an empty local cache_when the app checks if some data was set more than a day ago_then isCachedObjectNullOrOlderThan24H returns null`() =
        runTest {
            val isCachedObjectNullOrOlderThan24H = getIsCachedObjectNullOrOlderThan24H(
                getLocalCacheEntityReturn = null
            )
            val actualResult = isCachedObjectNullOrOlderThan24H(localCache.uid, localCache.section)

            assertEquals(null, actualResult)
        }

    @Test
    fun `given an older local cache_when the app checks if some data was set more than a day ago_then isCachedObjectNullOrOlderThan24H returns true`() =
        runTest {
            val localCache = localCache.copy(timestamp = 123L)
            val isCachedObjectNullOrOlderThan24H = getIsCachedObjectNullOrOlderThan24H(
                cacheArg = localCache,
                getLocalCacheEntityReturn = localCache.toEntity()
            )
            val actualResult = isCachedObjectNullOrOlderThan24H(localCache.uid, localCache.section)

            assertEquals(true, actualResult)
        }

    @Test
    fun `given a recent local cache_when the app checks if some data was set more than a day ago_then isCachedObjectNullOrOlderThan24H returns false`() =
        runTest {
            val isCachedObjectNullOrOlderThan24H = getIsCachedObjectNullOrOlderThan24H()
            val actualResult = isCachedObjectNullOrOlderThan24H(localCache.uid, localCache.section)

            assertEquals(false, actualResult)
        }
}
