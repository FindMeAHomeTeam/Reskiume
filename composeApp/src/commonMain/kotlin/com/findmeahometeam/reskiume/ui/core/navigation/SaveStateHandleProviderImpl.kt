package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.toRoute
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

class SaveStateHandleProviderImpl(
    private val savedStateHandle: SavedStateHandle
): SaveStateHandleProvider {
    override fun <T : Any> provideObjectRoute(
        route: KClass<T>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
    ): T {
        return savedStateHandle.toRoute(route = route, typeMap = typeMap)
    }
}
