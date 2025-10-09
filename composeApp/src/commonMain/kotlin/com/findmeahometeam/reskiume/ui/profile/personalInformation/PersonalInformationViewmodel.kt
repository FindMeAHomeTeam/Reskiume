package com.findmeahometeam.reskiume.ui.profile.personalInformation

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.domain.usecases.SignOut

class PersonalInformationViewmodel(
    private val signOut: SignOut
): ViewModel() {

    fun logOut() {
        signOut()
        removeUserContent()
    }

    private fun removeUserContent() {
        // TODO
    }

}