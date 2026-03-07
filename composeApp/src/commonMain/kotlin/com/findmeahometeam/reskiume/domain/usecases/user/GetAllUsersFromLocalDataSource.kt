package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class GetAllUsersFromLocalDataSource(private val repository: LocalUserRepository) {

    suspend operator fun invoke(): List<User> = repository.getAllUsers().map { it.toDomain() }
}
