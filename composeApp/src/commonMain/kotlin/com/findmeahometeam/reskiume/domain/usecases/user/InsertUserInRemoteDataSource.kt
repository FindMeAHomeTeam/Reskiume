package com.findmeahometeam.reskiume.domain.usecases.user

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository

class InsertUserInRemoteDataSource(private val repository: RealtimeDatabaseRemoteUserRepository) {
    suspend operator fun invoke(user: User, onInsertRemoteUser: (result: DatabaseResult) -> Unit) =
        repository.insertRemoteUser(remoteUser = user.toData(), onInsertRemoteUser)
}
