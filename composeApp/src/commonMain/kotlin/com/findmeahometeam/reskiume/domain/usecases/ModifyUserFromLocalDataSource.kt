package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository

class ModifyUserFromLocalDataSource(private val repository: LocalRepository) {
    suspend operator fun invoke(user: User, onModifyUser: (Int) -> Unit) {
        repository.modifyUser(user, onModifyUser)
    }
}
