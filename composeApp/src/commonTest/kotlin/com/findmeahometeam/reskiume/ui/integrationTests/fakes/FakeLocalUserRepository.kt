package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository

class FakeLocalUserRepository(
    private var localUserList: MutableList<User> = mutableListOf()
): LocalUserRepository {

    override suspend fun insertUser(
        user: User,
        onInsertUser: (rowId: Long) -> Unit
    ) {
        val localUser = localUserList.firstOrNull{ it.uid == user.uid }
        if (localUser == null) {
            localUserList.add(user)
            onInsertUser(1L)
        } else {
            onInsertUser(0)
        }
    }

    override suspend fun modifyUser(
        user: User,
        onModifyUser: (rowsUpdated: Int) -> Unit
    ) {
        val localUser = localUserList.firstOrNull{ it.uid == user.uid }
        if (localUser == null) {
            onModifyUser(0)
        } else {
            localUserList[localUserList.indexOf(localUser)] = user
            onModifyUser(1)
        }
    }

    override suspend fun deleteUsers(
        userUid: String,
        onDeletedUser: (rowsDeleted: Int) -> Unit
    ) {
        val localUser = localUserList.firstOrNull{ it.uid == userUid }
        if (localUser == null) {
            onDeletedUser(0)
        } else {
            localUserList.remove(localUser)
            onDeletedUser(1)
        }
    }

    override suspend fun getUser(uid: String): User? {
        return localUserList.firstOrNull{ it.uid == uid }
    }
}
