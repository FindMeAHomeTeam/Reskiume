package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository

class DeleteUserFromLocalDataSource(private val repository: LocalRepository) {
    suspend operator fun invoke(userUid: String, onDeletedUser: (rowsDeleted: Int) -> Unit) {
        repository.deleteUser(userUid, onDeletedUser)
    }
}
