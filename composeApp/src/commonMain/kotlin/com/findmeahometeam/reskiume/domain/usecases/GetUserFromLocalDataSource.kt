package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class GetUserFromLocalDataSource(private val repository: LocalUserRepository) {
    suspend operator fun invoke(userUid: String): User? = repository.getUser(userUid)
}
