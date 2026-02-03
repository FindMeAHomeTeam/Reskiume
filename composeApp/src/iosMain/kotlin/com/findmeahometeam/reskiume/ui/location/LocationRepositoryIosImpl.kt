package com.findmeahometeam.reskiume.ui.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSError
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.darwin.NSObject
import platform.darwin.NSObjectProtocol
import kotlin.coroutines.resume

class LocationRepositoryIosImpl : LocationRepository {

    override fun observeIfLocationEnabledFlow(): Flow<Boolean> = callbackFlow {

        fun isLocationEnabled(): Boolean = CLLocationManager.locationServicesEnabled()

        val locationManager: CLLocationManager = CLLocationManager().apply {

            delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didChangeAuthorizationStatus: CLAuthorizationStatus
                ) {
                    trySend(isLocationEnabled())
                }
            }
        }

        // Emit initial value
        trySend(isLocationEnabled())

        // Also re-check when coming back from Settings
        val center: NSNotificationCenter = NSNotificationCenter.defaultCenter
        val token: NSObjectProtocol = center.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            trySend(isLocationEnabled())
        }

        awaitClose {
            center.removeObserver(token)
            locationManager.delegate = null
        }
    }

    override fun requestEnableLocation(onResult: (isEnabled: Boolean) -> Unit) {
        val locationManager = CLLocationManager()

        // Check authorization status
        val status: CLAuthorizationStatus = CLLocationManager.authorizationStatus()

        when (status) {
            kCLAuthorizationStatusNotDetermined -> {

                // Set up delegate to receive authorization response
                locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                    override fun locationManager(
                        manager: CLLocationManager,
                        didChangeAuthorizationStatus: CLAuthorizationStatus
                    ) {
                        val isAuthorized = didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                                didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways
                        onResult(isAuthorized)
                    }
                }
                locationManager.requestWhenInUseAuthorization()
            }

            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                onResult(true)
            }

            else -> {
                // Denied or restricted - user must enable in Settings manually
                onResult(false)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getLocation(): Pair<Double, Double> {
        val location: CLLocation = getCurrentLocation() ?: return Pair(0.0, 0.0)

        location.coordinate.useContents {
            return Pair(longitude, latitude)
        }
    }

    private suspend fun getCurrentLocation(): CLLocation? {

        return suspendCancellableCoroutine { continuation ->

            val locationManager = CLLocationManager()
            var isCompleted = false

            fun onComplete(value: CLLocation?) {
                if (isCompleted) return

                isCompleted = true
                locationManager.stopUpdatingLocation()
                locationManager.delegate = null
                continuation.resume(value)
            }
            locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val location: CLLocation? = didUpdateLocations.firstOrNull() as? CLLocation
                    onComplete(location)
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    onComplete(null)
                }
            }
            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
                locationManager.delegate = null
            }
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()
        }
    }
}
