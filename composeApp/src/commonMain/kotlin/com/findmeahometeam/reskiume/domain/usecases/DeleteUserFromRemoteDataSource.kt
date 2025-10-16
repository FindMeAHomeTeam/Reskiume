package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.domain.repository.remote.RealtimeDatabaseRepository

class DeleteUserFromRemoteDataSource(private val repository: RealtimeDatabaseRepository) {
    operator fun invoke(userUid: String, onDeleteRemoteUser: (result: DatabaseResult) -> Unit) =
        repository.deleteRemoteUser(userUid, onDeleteRemoteUser)
}