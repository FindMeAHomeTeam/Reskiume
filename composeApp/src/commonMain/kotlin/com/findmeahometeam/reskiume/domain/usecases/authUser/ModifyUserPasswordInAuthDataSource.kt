package com.findmeahometeam.reskiume.domain.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class ModifyUserPasswordInAuthDataSource(private val repository: AuthRepository) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        onUpdatedUserPassword: (error: String) -> Unit
    ) = repository.updateUserPassword(currentPassword, newPassword, onUpdatedUserPassword)
}
