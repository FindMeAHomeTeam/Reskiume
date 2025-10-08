package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.AuthRepository

class DeleteUser(private val repository: AuthRepository) {
    suspend operator fun invoke(password: String, onDeleteUser: (String, String) -> Unit) =
        repository.deleteUser(password, onDeleteUser)
}