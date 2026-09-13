package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.user.SubscriptionEntityForUser
import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import kotlinx.coroutines.flow.Flow

interface LocalUserRepository {
    suspend fun upsertUser(
        user: UserEntity,
        subscriptions: List<SubscriptionEntityForUser>
    ): Flow<Boolean>

    suspend fun deleteUsers(userUid: String, onDeletedUser: (rowsDeleted: Int) -> Unit)

    suspend fun insertSubscription(
        subscriptionEntityForUser: SubscriptionEntityForUser,
        onInsertSubscription: suspend (rowId: Long) -> Unit
    )

    suspend fun deleteSubscription(
        subscriptionId: String,
        onDeletedSubscription: (rowsDeleted: Int) -> Unit
    )

    fun getUser(uid: String): Flow<UserWithAllSubscriptionData?>

    fun getAllUsers(): Flow<List<UserWithAllSubscriptionData>>
}
