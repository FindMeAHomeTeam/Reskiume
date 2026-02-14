package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.firstOrNull

class ModifyUserInLocalDataSource(
    private val manageImagePath: ManageImagePath,
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, onModifyUser: (rowsUpdated: Int) -> Unit) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(user.image)

        localUserRepository.modifyUser(
            user.copy(
                savedBy = getMyUid(),
                image = imageFileName
            ),
            onModifyUser
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
