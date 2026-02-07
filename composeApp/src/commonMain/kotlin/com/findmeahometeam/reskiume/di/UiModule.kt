package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProviderImpl
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.CheckAllFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.CheckAllAdviceViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllNonHumanAnimals.CheckAllNonHumanAnimalsViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsViewmodel
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal.CreateNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.ModifyNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.ui.util.StringProviderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val uiModule = module {
    factoryOf(::SaveStateHandleProviderImpl) bind SaveStateHandleProvider::class
    factoryOf(::StringProviderImpl) bind StringProvider::class
    viewModelOf(::HomeViewmodel)
    viewModelOf(::ProfileViewmodel)
    viewModelOf(::CreateAccountViewmodel)
    viewModelOf(::LoginAccountViewmodel)
    viewModelOf(::ModifyAccountViewmodel)
    viewModelOf(::DeleteAccountViewmodel)
    viewModelOf(::CheckReviewsViewmodel)
    singleOf(::CheckActivistUtil)
    viewModelOf(::CheckAllNonHumanAnimalsViewmodel)
    viewModelOf(::ModifyNonHumanAnimalViewmodel)
    viewModelOf(::CheckNonHumanAnimalViewmodel)
    singleOf(::CheckNonHumanAnimalUtilImpl) bind CheckNonHumanAnimalUtil::class
    viewModelOf(::CreateNonHumanAnimalViewmodel)
    singleOf(::DeleteNonHumanAnimalUtil)
    viewModelOf(::CheckAllAdviceViewmodel)
    viewModelOf(::CheckAllFosterHomesViewmodel)
    viewModelOf(::CheckAllMyFosterHomesViewmodel)
    singleOf(::CheckAllMyFosterHomesUtilImpl) bind CheckAllMyFosterHomesUtil::class
    singleOf(::PlaceUtil)
}
