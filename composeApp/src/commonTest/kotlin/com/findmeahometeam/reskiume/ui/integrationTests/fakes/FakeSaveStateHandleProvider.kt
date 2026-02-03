package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import androidx.navigation.NavType
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.user
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
class FakeSaveStateHandleProvider(
    private val objectRoute: Any = CheckReviews(user.uid)
): SaveStateHandleProvider {
    override fun <T : Any> provideObjectRoute(
        route: KClass<T>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>
    ): T {
        return objectRoute as T
    }
}
