package gs.location_scanner.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.Location.FORMAT_SECONDS
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import gs.location_scanner.data.GPSLocationStats


class GPSScannerService {

    private lateinit var locationClient: FusedLocationProviderClient

    val gpsScanStatus: MutableLiveData<ScanStatus> = MutableLiveData(ScanStatus.NO_STATUS)

    val gpsScanResults: MutableLiveData<GPSLocationStats> = MutableLiveData(null)

    fun setup(context: Context) {
        locationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) {
        gpsScanStatus.postValue(ScanStatus.IN_PROGRESS)
        gpsScanResults.postValue(null)
        if (hasPermissions(context)) {
            if (isLocationEnabled(context)) {
                locationClient
                    .getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                    .addOnCompleteListener {
                        if (it != null) {
                            gpsScanResults.postValue(
                                GPSLocationStats(
                                    Location.convert(it.result.latitude, FORMAT_SECONDS),
                                    Location.convert(it.result.longitude, FORMAT_SECONDS)
                                )
                            )
                            gpsScanStatus.postValue(ScanStatus.LAST_SCAN_SUCCESS)
                        } else {
                            //Location is null, try to request a new one
                            gpsScanStatus.postValue(ScanStatus.LAST_SCAN_FAILED)
                        }
                    }
            } else {
                //Location not enabled
                gpsScanStatus.postValue(ScanStatus.LAST_SCAN_FAILED)
            }
        } else {
            //Need to get permissions
            gpsScanStatus.postValue(ScanStatus.LAST_SCAN_FAILED)
        }
    }

    private fun hasPermissions(context: Context): Boolean {
        return ActivityCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}