package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.RealtimeDatabaseRepository

class InsertUserToRemoteDataSource(private val repository: RealtimeDatabaseRepository) {
    operator fun invoke(user: User, onInsertRemoteUser: (result: DatabaseResult) -> Unit) =
        repository.insertRemoteUser(remoteUser = user.toData(), onInsertRemoteUser)
}
