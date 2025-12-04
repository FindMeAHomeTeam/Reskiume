package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository

class ModifyUserInRemoteDataSource(private val repository: RealtimeDatabaseRemoteUserRepository) {
    suspend operator fun invoke(user: User, onUpdateRemoteUser: (result: DatabaseResult) -> Unit) =
        repository.updateRemoteUser(remoteUser = user.toData(), onUpdateRemoteUser)
}
