package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class InsertUserInLocalDataSource(
    private val authRepository: AuthRepository,
    private val manageImagePath: ManageImagePath,
    private val localUserRepository: LocalUserRepository,
    private val fCMSubscriberRepository: FCMSubscriberRepository,
    private val log: Log
) {
    operator fun invoke(user: User): Flow<Boolean> = flow {

        val myUid = getMyUid()
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(user.image)

        var isSuccess = localUserRepository.upsertUser(
            user = user.copy(
                savedBy = myUid,
                image = imageFileName
            ).toEntity(),
            subscriptions = if (user.uid == myUid && user.subscriptions.isNotEmpty()) {
                user.subscriptions.map { it.toEntity() }
            } else {
                emptyList()
            }
        ).first()

        if (isSuccess) {

            if (user.uid == myUid && user.subscriptions.isNotEmpty()) {
                isSuccess = subscribeToAllTopics(user)
            }
            emit(isSuccess)
        } else {
            log.e(
                "InsertUserInLocalDataSource",
                "invoke: failed to insert the user ${user.uid} in the local data source"
            )
            emit(false)
        }
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun subscribeToAllTopics(user: User): Boolean {
        var isSuccess = false
        user.subscriptions.forEach { subscription ->

            isSuccess = fCMSubscriberRepository.subscribeToTopic(subscription.topic).first()
        }
        return isSuccess
    }
}
