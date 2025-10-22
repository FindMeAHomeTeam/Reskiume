package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository

class LocalRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalRepository {
    override suspend fun insertUser(user: User, onInsertUser: (rowId: Long) -> Unit) {
        onInsertUser(reskiumeDatabase.getUserDao().insertUser(user.toEntity()))
    }

    override suspend fun modifyUser(user: User, onModifyUser: (Int) -> Unit) {
        onModifyUser(reskiumeDatabase.getUserDao().modifyUser(user.toEntity()))
    }

    override suspend fun deleteUser(userUid: String, onDeletedUser: (Int) -> Unit) {
        onDeletedUser(reskiumeDatabase.getUserDao().deleteUser(userUid))
    }

    override suspend fun getUser(uid: String): User? =
        reskiumeDatabase.getUserDao().getUser(uid)?.toDomain()
}
