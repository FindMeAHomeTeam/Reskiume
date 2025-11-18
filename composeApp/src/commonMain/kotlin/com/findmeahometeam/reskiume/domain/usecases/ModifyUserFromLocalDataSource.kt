package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class ModifyUserFromLocalDataSource(private val repository: LocalUserRepository) {
    suspend operator fun invoke(user: User, onModifyUser: (Int) -> Unit) {
        repository.modifyUser(user, onModifyUser)
    }
}
