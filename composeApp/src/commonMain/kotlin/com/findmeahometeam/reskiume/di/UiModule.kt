package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProviderImpl
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.CheckAllFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.PlaceUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtilImpl
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome.CreateFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtilImpl
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.ModifyFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.home.HomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.CheckAllAdviceViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkMyAllNonHumanAnimals.CheckAllMyNonHumanAnimalsViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtilImpl
import com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal.CreateNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.ModifyNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.rescueEvents.checkAllRescueEvents.CheckAllRescueEventsViewmodel
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtilImpl
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.rescueEvents.createRescueEvent.CreateRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtilImpl
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.ModifyRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.ui.util.StringProviderImpl
import com.findmeahometeam.reskiume.ui.util.fcm.MessagingServiceViewModel
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtilImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
    viewModel {
        DeleteAccountViewmodel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModelOf(::CheckReviewsViewmodel)
    singleOf(::CheckReviewsUtilImpl) bind CheckReviewsUtil::class
    singleOf(::CheckActivistUtilImpl) bind CheckActivistUtil::class
    viewModelOf(::CheckAllMyNonHumanAnimalsViewmodel)
    viewModelOf(::ModifyNonHumanAnimalViewmodel)
    viewModelOf(::CheckNonHumanAnimalViewmodel)
    singleOf(::CheckNonHumanAnimalUtilImpl) bind CheckNonHumanAnimalUtil::class
    viewModelOf(::CreateNonHumanAnimalViewmodel)
    singleOf(::DeleteNonHumanAnimalUtilImpl) bind DeleteNonHumanAnimalUtil::class
    viewModelOf(::CheckAllAdviceViewmodel)
    viewModelOf(::CheckAllFosterHomesViewmodel)
    viewModelOf(::CheckAllMyFosterHomesViewmodel)
    singleOf(::CheckAllMyFosterHomesUtilImpl) bind CheckAllMyFosterHomesUtil::class
    viewModelOf(::ModifyFosterHomeViewmodel)
    singleOf(::DeleteFosterHomeUtilImpl) bind DeleteFosterHomeUtil::class
    singleOf(::PlaceUtil)
    viewModelOf(::CreateFosterHomeViewmodel)
    viewModelOf(::CheckFosterHomeViewmodel)
    singleOf(::CheckFosterHomeUtilImpl) bind CheckFosterHomeUtil::class
    viewModelOf(::CheckAllMyRescueEventsViewmodel)
    singleOf(::CheckAllMyRescueEventsUtilImpl) bind CheckAllMyRescueEventsUtil::class
    viewModelOf(::CreateRescueEventViewmodel)
    viewModelOf(::ModifyRescueEventViewmodel)
    singleOf(::DeleteRescueEventUtilImpl) bind DeleteRescueEventUtil::class
    viewModelOf(::CheckAllRescueEventsViewmodel)
    viewModelOf(::CheckRescueEventViewmodel)
    singleOf(::CheckRescueEventUtilImpl) bind CheckRescueEventUtil::class
    singleOf(::SubscriptionManagerUtilImpl) bind SubscriptionManagerUtil::class
    viewModelOf(::MessagingServiceViewModel)
}
