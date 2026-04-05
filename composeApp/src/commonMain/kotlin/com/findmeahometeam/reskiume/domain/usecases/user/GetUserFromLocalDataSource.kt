package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserFromLocalDataSource(private val repository: LocalUserRepository) {

    operator fun invoke(userUid: String): Flow<User?> = repository.getUser(userUid)
        .map { userWithAllSubscriptionData: UserWithAllSubscriptionData? ->

            userWithAllSubscriptionData?.userEntity?.toDomain(
                subscriptions = userWithAllSubscriptionData.allSubscriptions.map { it.toDomain() }
            )
        }
}
