package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.RealtimeDatabaseRepository

class ModifyUserFromRemoteDataSource(private val repository: RealtimeDatabaseRepository) {
    operator fun invoke(user: User, onUpdateRemoteUser: (result: DatabaseResult) -> Unit) =
        repository.updateRemoteUser(remoteUser = user.toData(), onUpdateRemoteUser)
}
