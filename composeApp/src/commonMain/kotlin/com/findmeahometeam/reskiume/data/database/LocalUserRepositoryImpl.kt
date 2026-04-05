package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.user.SubscriptionEntityForUser
import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import kotlinx.coroutines.flow.Flow

class LocalUserRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalUserRepository {

    override suspend fun insertUser(
        user: UserEntity,
        onInsertUser: suspend (rowId: Long) -> Unit
    ) {
        onInsertUser(reskiumeDatabase.getUserDao().insertUser(user))
    }

    override suspend fun insertSubscription(
        subscriptionEntityForUser: SubscriptionEntityForUser,
        onInsertSubscription: (rowId: Long) -> Unit
    ) {
        onInsertSubscription(reskiumeDatabase.getUserDao().insertSubscription(subscriptionEntityForUser))
    }

    override suspend fun modifyUser(
        user: UserEntity,
        onModifyUser: suspend (rowsUpdated: Int) -> Unit
    ) {
        onModifyUser(reskiumeDatabase.getUserDao().modifyUser(user))
    }

    override suspend fun deleteUsers(userUid: String, onDeletedUser: (Int) -> Unit) {
        onDeletedUser(reskiumeDatabase.getUserDao().deleteUsers(userUid))
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
