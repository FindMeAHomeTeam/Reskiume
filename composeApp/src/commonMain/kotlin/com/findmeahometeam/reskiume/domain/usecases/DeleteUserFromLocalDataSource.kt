package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class DeleteUserFromLocalDataSource(private val repository: LocalUserRepository) {
    suspend operator fun invoke(userUid: String, onDeletedUser: (rowsDeleted: Int) -> Unit) {
        repository.deleteUser(userUid, onDeletedUser)
    }
}
