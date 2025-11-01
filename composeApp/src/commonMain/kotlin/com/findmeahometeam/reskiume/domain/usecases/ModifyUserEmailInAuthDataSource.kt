package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class ModifyUserEmailInAuthDataSource(private val repository: AuthRepository) {
    suspend operator fun invoke(
        password: String,
        newEmail: String,
        onUpdatedUserEmail: (error: String) -> Unit
    ) = repository.updateUserEmail(password, newEmail, onUpdatedUserEmail)
}
