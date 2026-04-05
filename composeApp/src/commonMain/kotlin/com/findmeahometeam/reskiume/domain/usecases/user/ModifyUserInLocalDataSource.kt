package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.Subscription
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class ModifyUserInLocalDataSource(
    private val manageImagePath: ManageImagePath,
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedUser: User,
        onModifyUser: (isUpdated: Boolean) -> Unit
    ) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(updatedUser.image)

        localUserRepository.modifyUser(
            updatedUser.copy(
                savedBy = getMyUid(),
                image = imageFileName
            ).toEntity(),
            onModifyUser = { rowsUpdated ->
                if (rowsUpdated > 0) {

                    val isSuccess = manageAllSubscriptions(updatedUser)
                    onModifyUser(isSuccess)
                } else {
                    onModifyUser(false)
                }
            }
        )
    }

    private suspend fun manageAllSubscriptions(updatedUser: User): Boolean {

        var isSuccess = true

        val previousUser = localUserRepository.getUser(updatedUser.uid).first()!!

        val previousSubscriptions =
            previousUser.allSubscriptions.map { it.toDomain() }.toSet()

        val updatedSubscriptions =
            updatedUser.subscriptions.toSet()

        val allSubscriptionsToManage: Set<Subscription> =
            (previousSubscriptions - updatedSubscriptions) +
                    (updatedSubscriptions - previousSubscriptions)

        allSubscriptionsToManage.forEach { subscriptionToManage ->
            if (isSuccess) {
                if (updatedSubscriptions.contains(subscriptionToManage)) {

                    val subscriptionEntity =
                        updatedUser.subscriptions.first {
                            it.subscriptionId == subscriptionToManage.subscriptionId
                        }.toEntity()

                    localUserRepository.insertSubscription(
                        subscriptionEntity,
                        onInsertSubscription = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "ModifyUserInLocalDataSource",
                                    "manageAllSubscriptions: inserted the subscription id ${subscriptionEntity.subscriptionId} for the user ${subscriptionEntity.uid} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyUserInLocalDataSource",
                                    "manageAllSubscriptions: failed to insert the subscription id ${subscriptionEntity.subscriptionId} for the user ${subscriptionEntity.uid} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                } else {

                    val subscriptionEntity =
                        previousUser.allSubscriptions.first {
                            it.subscriptionId == subscriptionToManage.subscriptionId
                        }

                    localUserRepository.deleteSubscription(
                        subscriptionEntity.subscriptionId,
                        onDeletedSubscription = { rowsDeleted ->
                            if (rowsDeleted > 0) {
                                log.d(
                                    "ModifyUserInLocalDataSource",
                                    "manageAllSubscriptions: deleted the subscription id ${subscriptionEntity.subscriptionId} for the user ${subscriptionEntity.uid} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyUserInLocalDataSource",
                                    "manageAllSubscriptions: failed to delete the subscription id ${subscriptionEntity.subscriptionId} for the user ${subscriptionEntity.uid} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                }
            }
        }
        return isSuccess
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
