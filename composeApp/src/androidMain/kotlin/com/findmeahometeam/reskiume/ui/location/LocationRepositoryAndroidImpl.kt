package com.findmeahometeam.reskiume.ui.location

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import com.findmeahometeam.reskiume.ActivityHolder
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

private const val WAITING_TIME: Int = 2 * 60 * 1000 // 2 min

class LocationRepositoryAndroidImpl : LocationRepository {

    /**
     * Resolves the current ComponentActivity lazily via ActivityHolder so that this singleton
     * never holds a stale reference after the activity is destroyed (e.g. on back-press).
     */
    private val componentActivity: ComponentActivity?
        get() = ActivityHolder.activityOrNull

    override fun observeIfLocationEnabledFlow(): Flow<Boolean> = callbackFlow {

        val componentActivity = componentActivity ?: run {
            close()
            return@callbackFlow
        }

        trySend(isLocationEnabled(componentActivity))

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val current = this@LocationRepositoryAndroidImpl.componentActivity ?: return
                trySend(isLocationEnabled(current))
            }
        }
        componentActivity.registerReceiver(
            receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

        awaitClose {
            componentActivity.unregisterReceiver(receiver)
        }
    }

    private fun isLocationEnabled(componentActivity: ComponentActivity): Boolean {
        val locationManager =
            componentActivity.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun requestEnableLocation(onResult: (isEnabled: Boolean) -> Unit) {
        val componentActivity = componentActivity ?: run {
            onResult(false)
            return
        }

        if (isLocationEnabled(componentActivity)) {
            onResult(true)
            return
        }

        // Open location settings
        var launcher: ActivityResultLauncher<Intent>? = null
        launcher = componentActivity.registerActivityResultLauncher(
            contract = ActivityResultContracts.StartActivityForResult(),
            callback = {
                val current = this@LocationRepositoryAndroidImpl.componentActivity ?: return@registerActivityResultLauncher
                onResult(isLocationEnabled(current))
                launcher?.unregister()
            }
        )
        launcher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun <I, O> ComponentActivity.registerActivityResultLauncher(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I> {

        val key = UUID.randomUUID().toString()
        return activityResultRegistry.register(key, contract, callback)
    }

    @OptIn(ExperimentalTime::class)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun getLocation(): Pair<Double, Double> {

        val componentActivity = componentActivity ?: return Pair(0.0, 0.0)

        val locationManager: LocationManager =
            componentActivity.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return Pair(0.0, 0.0)

        val providers: List<String> = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        ).filter { locationManager.isProviderEnabled(it) }

        val locations: List<Location> = providers.mapNotNull {
            locationManager.getLastKnownLocation(it)
        }

        val bestLocation: Location? = locations
            .filter { it.time - Clock.System.now().epochSeconds.milliseconds.inWholeMilliseconds < WAITING_TIME }
            .minByOrNull { it.accuracy }

        return if (providers.isEmpty()) {

            Pair(0.0, 0.0)
        } else if (bestLocation == null) {

            requestSingleUpdate(providers, locationManager).first()
        } else {

            Pair(bestLocation.longitude, bestLocation.latitude)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    private fun requestSingleUpdate(
        providers: List<String>,
        locationManager: LocationManager
    ): Flow<Pair<Double, Double>> = callbackFlow {

        var bestLocation: Location? = null

        val listener = LocationListener { location ->

            if (bestLocation == null || location.accuracy < bestLocation!!.accuracy) {

                bestLocation = location
                trySend(Pair(bestLocation.longitude, bestLocation.latitude))
                close()
            }
        }
        providers.forEach { provider ->

            locationManager.requestLocationUpdates(
                provider,
                0L,
                0f,
                listener
            )
        }

        awaitClose { locationManager.removeUpdates(listener) }
    }
}
