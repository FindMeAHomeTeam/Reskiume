package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository

class LocalRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalRepository {
    override suspend fun insertUser(user: User) {
        reskiumeDatabase.getUserDao().insertUser(user.toEntity())
    }

    override suspend fun modifyUser(user: User) {
        reskiumeDatabase.getUserDao().modifyUser(user.toEntity())
    }

    override suspend fun deleteUser(userUid: String) {
        reskiumeDatabase.getUserDao().deleteUser(userUid)
    }

    override suspend fun getUser(uid: String): User =
        reskiumeDatabase.getUserDao().getUser(uid).toDomain()
}
