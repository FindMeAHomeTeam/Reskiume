package com.findmeahometeam.reskiume.usecases.localCache

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.localCache
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
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetDataByManagingObjectLocalCacheTimestampTest {

    private lateinit var localCacheRepository: LocalCacheRepository

    private val log: Log = mock {
        every { d(any(), any()) } returns Unit
        every { e(any(), any()) } returns Unit
    }

    private val onInsertLocalCache = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCache = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private fun getUseCaseGetDataByManagingObjectLocalCacheTimestamp(
        cacheArg: LocalCache = localCache,
        getLocalCacheEntityReturn: LocalCacheEntity? = localCache.toEntity(),
        rowIdInsertedArg: Long = 1L,
        rowsUpdatedArg: Int = 1
    ): GetDataByManagingObjectLocalCacheTimestamp {
        localCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    cacheArg.uid,
                    cacheArg.section
                )
            } returns getLocalCacheEntityReturn

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCache)
                )
            } calls { onInsertLocalCache.get().invoke(rowIdInsertedArg) }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCache)
                )
            } calls { onModifyLocalCache.get().invoke(rowsUpdatedArg) }
        }
        return GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log)
    }

    @Test
    fun `given an empty local cache_when the app checks data and inserts the local cache_then getLocalCacheEntity insertLocalCacheEntity and logD are called`() =
        runTest {
            val getDataByManagingObjectLocalCacheTimestamp =
                getUseCaseGetDataByManagingObjectLocalCacheTimestamp(getLocalCacheEntityReturn = null)

            getDataByManagingObjectLocalCacheTimestamp(
                uid = user.uid,
                section = Section.REVIEWS,
                onCompletionInsertCache = { },
                onCompletionUpdateCache = { },
                onVerifyCacheIsRecent = { }
            )
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(user.uid, Section.REVIEWS)
                localCacheRepository.insertLocalCacheEntity(any(), any())
                log.d(any(), any())
            }
        }

    @Test
    fun `given an empty local cache_when the app checks data but there is an error inserting the local cache_then getLocalCacheEntity insertLocalCacheEntity and logE are called`() =
        runTest {
            val getDataByManagingObjectLocalCacheTimestamp =
                getUseCaseGetDataByManagingObjectLocalCacheTimestamp(
                    getLocalCacheEntityReturn = null,
                    rowIdInsertedArg = 0
                )

            getDataByManagingObjectLocalCacheTimestamp(
                uid = user.uid,
                section = Section.REVIEWS,
                onCompletionInsertCache = { },
                onCompletionUpdateCache = { },
                onVerifyCacheIsRecent = { }
            )
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(user.uid, Section.REVIEWS)
                localCacheRepository.insertLocalCacheEntity(any(), any())
                log.e(any(), any())
            }
        }

    @Test
    fun `given an older local cache_when the app checks data and it was set more than a day ago_then getLocalCacheEntity modifyLocalCacheEntity and logD are called`() =
        runTest {
            val localCache = localCache.copy(timestamp = 123L)
            val getDataByManagingObjectLocalCacheTimestamp =
                getUseCaseGetDataByManagingObjectLocalCacheTimestamp(
                    cacheArg = localCache,
                    getLocalCacheEntityReturn = localCache.toEntity()
                )

            getDataByManagingObjectLocalCacheTimestamp(
                uid = user.uid,
                section = Section.REVIEWS,
                onCompletionInsertCache = { },
                onCompletionUpdateCache = { },
                onVerifyCacheIsRecent = { }
            )
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(user.uid, Section.REVIEWS)
                localCacheRepository.modifyLocalCacheEntity(any(), any())
                log.d(any(), any())
            }
        }

    @Test
    fun `given an older local cache_when the app checks data and it was set more than a day ago but there is an error updating it_then getLocalCacheEntity modifyLocalCacheEntity and logE are called`() =
        runTest {
            val localCache = localCache.copy(timestamp = 123L)
            val getDataByManagingObjectLocalCacheTimestamp =
                getUseCaseGetDataByManagingObjectLocalCacheTimestamp(
                    cacheArg = localCache,
                    getLocalCacheEntityReturn = localCache.toEntity(),
                    rowsUpdatedArg = 0
                )

            getDataByManagingObjectLocalCacheTimestamp(
                uid = user.uid,
                section = Section.REVIEWS,
                onCompletionInsertCache = { },
                onCompletionUpdateCache = { },
                onVerifyCacheIsRecent = { }
            )
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(user.uid, Section.REVIEWS)
                localCacheRepository.modifyLocalCacheEntity(any(), any())
                log.e(any(), any())
            }
        }

    @Test
    fun `given an recent local cache_when the app checks data_then getLocalCacheEntity and logD are called`() =
        runTest {
            val getDataByManagingObjectLocalCacheTimestamp =
                getUseCaseGetDataByManagingObjectLocalCacheTimestamp()

            getDataByManagingObjectLocalCacheTimestamp(
                uid = user.uid,
                section = Section.REVIEWS,
                onCompletionInsertCache = { },
                onCompletionUpdateCache = { },
                onVerifyCacheIsRecent = { }
            )
            verifySuspend {
                localCacheRepository.getLocalCacheEntity(user.uid, Section.REVIEWS)
                log.d(any(), any())
            }
            verifySuspend(exactly(0)) {
                localCacheRepository.modifyLocalCacheEntity(any(), any())
            }
        }
}
