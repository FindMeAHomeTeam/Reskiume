package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.domain.model.User

interface LocalRepository {
    suspend fun insertUser(user: User, onInsertUser: (rowId: Long) -> Unit)
    suspend fun modifyUser(user: User, onModifyUser: (rowsUpdated: Int) -> Unit)
    suspend fun deleteUser(userUid: String, onDeletedUser: (rowsDeleted: Int) -> Unit)
    suspend fun getUser(uid: String): User?
}
