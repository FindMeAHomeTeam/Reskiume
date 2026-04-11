package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.database.entity.user.SubscriptionEntityForUser
import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalUserRepository(
    private var localUserWithAllSubscriptionDataList: MutableList<UserWithAllSubscriptionData> = mutableListOf()
) : LocalUserRepository {

    override suspend fun insertUser(
        user: UserEntity,
        onInsertUser: suspend (rowId: Long) -> Unit
    ) {
        val localUserWithAllSubscriptionData =
            localUserWithAllSubscriptionDataList.firstOrNull { it.userEntity.uid == user.uid }
        if (localUserWithAllSubscriptionData == null) {
            localUserWithAllSubscriptionDataList.add(
                UserWithAllSubscriptionData(user, emptyList())
            )
            onInsertUser(1L)
        } else {
            onInsertUser(0)
        }
    }

    override suspend fun insertSubscription(
        subscriptionEntityForUser: SubscriptionEntityForUser,
        onInsertSubscription: suspend (rowId: Long) -> Unit
    ) {
        val allSubscriptions =
            localUserWithAllSubscriptionDataList.flatMap { userWithAllSubscriptionData ->

                userWithAllSubscriptionData.allSubscriptions.filter {
                    it.subscriptionId == subscriptionEntityForUser.subscriptionId
                }
            }

        if (allSubscriptions.isEmpty()) {

            val result = localUserWithAllSubscriptionDataList.map {
                it.copy(allSubscriptions = it.allSubscriptions + subscriptionEntityForUser)
            }
            localUserWithAllSubscriptionDataList.removeAll(
                localUserWithAllSubscriptionDataList
            )
            localUserWithAllSubscriptionDataList.addAll(result)
            onInsertSubscription(1L)
        } else {
            onInsertSubscription(0)
        }
    }

    override suspend fun modifyUser(
        user: UserEntity,
        onModifyUser: suspend (rowsUpdated: Int) -> Unit
    ) {
        val localUserWithAllSubscriptionData =
            localUserWithAllSubscriptionDataList.firstOrNull { it.userEntity.uid == user.uid }
        if (localUserWithAllSubscriptionData == null) {
            onModifyUser(0)
        } else {
            localUserWithAllSubscriptionDataList[localUserWithAllSubscriptionDataList.indexOf(
                localUserWithAllSubscriptionData
            )] = localUserWithAllSubscriptionData.copy(userEntity = user)
            onModifyUser(1)
        }
    }

    override suspend fun deleteUsers(
        userUid: String,
        onDeletedUser: (rowsDeleted: Int) -> Unit
    ) {
        val localUserWithAllSubscriptionData =
            localUserWithAllSubscriptionDataList.firstOrNull { it.userEntity.uid == userUid }

        if (localUserWithAllSubscriptionData == null) {
            onDeletedUser(0)
        } else {
            localUserWithAllSubscriptionDataList.remove(
                localUserWithAllSubscriptionData
            )
            onDeletedUser(1)
        }
    }

    override suspend fun deleteSubscription(
        subscriptionId: String,
        onDeletedSubscription: (rowsDeleted: Int) -> Unit
    ) {
        val subscription =
            localUserWithAllSubscriptionDataList.firstNotNullOfOrNull { userWithAllSubscriptionData ->
                userWithAllSubscriptionData.allSubscriptions.firstOrNull { it.subscriptionId == subscriptionId }
            }

        if (subscription == null) {
            onDeletedSubscription(0)
        } else {
            val result: List<UserWithAllSubscriptionData> =
                localUserWithAllSubscriptionDataList.map { userWithAllSubscriptionData ->

                    if (userWithAllSubscriptionData.allSubscriptions.contains(subscription)) {
                        userWithAllSubscriptionData.copy(
                            allSubscriptions = userWithAllSubscriptionData.allSubscriptions.minus(
                                subscription
                            )
                        )
                    } else {
                        userWithAllSubscriptionData
                    }
                }
            localUserWithAllSubscriptionDataList.removeAll(
                localUserWithAllSubscriptionDataList
            )
            localUserWithAllSubscriptionDataList.addAll(result)
            onDeletedSubscription(1)
        }
    }

    override fun getUser(uid: String): Flow<UserWithAllSubscriptionData?> {
        return flowOf(localUserWithAllSubscriptionDataList.firstOrNull { it.userEntity.uid == uid })
    }

    override fun getAllUsers(): Flow<List<UserWithAllSubscriptionData>> {
        return flowOf(localUserWithAllSubscriptionDataList)
    }
}
