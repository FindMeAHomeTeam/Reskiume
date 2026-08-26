package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.navigation.NavType
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface SaveStateHandleProvider {
    fun <T : Any> provideObjectRoute(
        route: KClass<T>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    ): T
}
