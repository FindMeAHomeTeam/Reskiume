package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllUsersFromLocalDataSource(private val repository: LocalUserRepository) {

    operator fun invoke(): Flow<List<User>> = repository.getAllUsers().map { list ->

        list.map { userWithAllSubscriptionData ->
            userWithAllSubscriptionData.userEntity.toDomain(
                subscriptions = userWithAllSubscriptionData.allSubscriptions.map { it.toDomain() }
            )
        }
    }
}
