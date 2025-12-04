package com.findmeahometeam.reskiume.domain.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class DeleteUserFromAuthDataSource(private val repository: AuthRepository) {
    suspend operator fun invoke(password: String, onDeleteUser: (String) -> Unit) =
        repository.deleteUser(password, onDeleteUser)
}
