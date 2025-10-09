package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.domain.model.User

interface LocalRepository {
    suspend fun insertUser(user: User)
    suspend fun modifyUser(user: User)
    suspend fun deleteUser(userUid: String)
    suspend fun getUser(uid: String): User
}