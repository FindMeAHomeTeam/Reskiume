package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.firstOrNull

class InsertUserInLocalDataSource(
    private val manageImagePath: ManageImagePath,
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, onInsertUser: (rowId: Long) -> Unit) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(user.image)

        localUserRepository.insertUser(
            user.copy(
                savedBy = getMyUid(),
                image = imageFileName
            ),
            onInsertUser
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
