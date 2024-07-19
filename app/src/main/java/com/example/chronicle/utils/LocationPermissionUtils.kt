package com.example.chronicle.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.example.chronicle.viewmodel.NavigationViewModel

/**
 * util to handle the location related method
 * **/
object LocationPermissionUtils {

    // handle the location permission request
    @RequiresApi(Build.VERSION_CODES.O)
    fun handleLocationPermissionRequest(
        requestPermissionLauncher: ActivityResultLauncher<String>,
    ) {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // check whether the location permission is grated
    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // get the current location and update the viewModel
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocationAndUpdateViewModel(context: Context, viewModel: NavigationViewModel) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val provider = when {
            isGpsEnabled -> LocationManager.GPS_PROVIDER
            isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

        provider?.let {
            val cancellationSignal: android.os.CancellationSignal? = null
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    LocationManagerCompat.getCurrentLocation(
                        locationManager,
                        it,
                        cancellationSignal,
                        ContextCompat.getMainExecutor(context)
                    ) { location: Location? ->
                        location?.let { loc ->
                            viewModel.updateLocationData(loc)
                        } ?: viewModel.clearLocationData()
                    }
                } catch (e: SecurityException) {
                    // Handle the SecurityException. For example, show a message to the user.
                    Log.e("LocationUtils", "SecurityException: ${e.message}")
                }
            } else {
                // The permission is not granted. Handle this case appropriately.
                Log.e("LocationUtils", "Location permission is not granted.")
            }
        } ?: viewModel.clearLocationData()
    }
}
