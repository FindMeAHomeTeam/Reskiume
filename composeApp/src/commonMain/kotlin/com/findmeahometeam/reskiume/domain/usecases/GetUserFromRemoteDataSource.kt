package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.RealtimeDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserFromRemoteDataSource(private val repository: RealtimeDatabaseRepository) {
    operator fun invoke(userUid: String): Flow<User?> = repository.getRemoteUser(userUid).map { it?.toData() }
}
