package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class InsertUserToLocalDataSource(private val repository: LocalUserRepository) {
    suspend operator fun invoke(user: User, onInsertUser: (rowId: Long) -> Unit) {
        repository.insertUser(user, onInsertUser)
    }
}
