package com.findmeahometeam.reskiume.ui.location

import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class LocationRepositoryIosImpl : LocationRepository {

    override fun isLocationEnabled(): Boolean =
        CLLocationManager.locationServicesEnabled()

    override fun requestEnableLocation(onResult: (Boolean) -> Unit) {
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
            locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val location: CLLocation? = didUpdateLocations.firstOrNull() as? CLLocation
                    locationManager.stopUpdatingLocation()
                    continuation.resume(location)
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    locationManager.stopUpdatingLocation()
                    continuation.resume(null)
                }
            }
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()
        }
    }
}
