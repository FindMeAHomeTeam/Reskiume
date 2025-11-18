package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRemoteUserRepository

class DeleteUserFromRemoteDataSource(private val repository: RealtimeDatabaseRemoteUserRepository) {
    operator fun invoke(userUid: String, onDeleteRemoteUser: (result: DatabaseResult) -> Unit) =
        repository.deleteRemoteUser(userUid, onDeleteRemoteUser)
}