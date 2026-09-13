package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.user.SubscriptionEntityForUser
import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LocalUserRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalUserRepository {

    override suspend fun upsertUser(
        user: UserEntity,
        subscriptions: List<SubscriptionEntityForUser>
    ): Flow<Boolean> = flow {

        reskiumeDatabase.getUserDao().upsertUser(
            user,
            subscriptions
        )
        emit(true)
    }.catch {
        emit(false)
    }

    override suspend fun deleteUsers(userUid: String, onDeletedUser: (Int) -> Unit) {
        onDeletedUser(reskiumeDatabase.getUserDao().deleteUsers(userUid))
    }

    override suspend fun insertSubscription(
        subscriptionEntityForUser: SubscriptionEntityForUser,
        onInsertSubscription: suspend (rowId: Long) -> Unit
    ) {
        onInsertSubscription(reskiumeDatabase.getUserDao().insertSubscription(subscriptionEntityForUser))
    }

    override suspend fun deleteSubscription(
        subscriptionId: String,
        onDeletedSubscription: (rowsDeleted: Int) -> Unit
    ) {
        onDeletedSubscription(reskiumeDatabase.getUserDao().deleteSubscription(subscriptionId))
    }

    override fun getUser(uid: String): Flow<UserWithAllSubscriptionData?> =
        reskiumeDatabase.getUserDao().getUser(uid)

    override fun getAllUsers(): Flow<List<UserWithAllSubscriptionData>> =
        reskiumeDatabase.getUserDao().getAllUsers()
}
