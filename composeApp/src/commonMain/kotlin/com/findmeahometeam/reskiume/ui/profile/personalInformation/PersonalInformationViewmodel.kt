package com.findmeahometeam.reskiume.ui.profile.personalInformation

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.domain.usecases.SignOutFromAuthDataSource

class PersonalInformationViewmodel(
    private val signOutFromAuthDataSource: SignOutFromAuthDataSource
): ViewModel() {

    fun logOut() {
        signOutFromAuthDataSource()
        removeUserContent()
    }

    private fun removeUserContent() {
        // TODO
    }

}