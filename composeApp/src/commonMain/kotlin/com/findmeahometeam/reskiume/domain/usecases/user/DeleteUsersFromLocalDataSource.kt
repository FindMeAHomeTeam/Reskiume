package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class DeleteUsersFromLocalDataSource(private val repository: LocalUserRepository) {
    suspend operator fun invoke(userUid: String, onDeletedUser: (rowsDeleted: Int) -> Unit) {
        repository.deleteUsers(userUid, onDeletedUser)
    }
}
