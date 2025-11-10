package com.findmeahometeam.reskiume.usecases

import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertUserToLocalDataSourceTest {

    val localRepository: LocalRepository = mock {
        everySuspend { insertUser(user, any()) } returns Unit
    }

    private val insertUserToLocalDataSource =
        InsertUserToLocalDataSource(localRepository)

    @Test
    fun `given a user_when the app saves it in the local data source_then it calls to insertUser`() =
        runTest {
            insertUserToLocalDataSource(user, {})
            verifySuspend {
                localRepository.insertUser(user, any())
            }
        }
}
