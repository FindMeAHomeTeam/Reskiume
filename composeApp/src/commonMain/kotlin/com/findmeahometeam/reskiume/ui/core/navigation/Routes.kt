package com.findmeahometeam.reskiume.ui.core.navigation

import kotlinx.serialization.Serializable

enum class Routes(val route: String) {
    HOME_SCREEN("homeScreen"),
    FOSTER_HOMES("fosterHomes"),
    RESCUE("rescue"),
    CHATS("chats"),
    PROFILE("profile"),
    CREATE_ACCOUNT("create account"),
    LOGIN_ACCOUNT("login account"),
    MODIFY_ACCOUNT("modify account"),
    DELETE_ACCOUNT("delete account"),

}

@Serializable
class CheckReviews(val uid: String)
