package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository

class GetUserFromLocalSource(private val repository: LocalRepository) {
    suspend operator fun invoke(userUid: String): User = repository.getUser(userUid)
}
