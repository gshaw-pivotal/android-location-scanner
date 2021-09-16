package gs.location_scanner.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.lifecycle.MutableLiveData

class WifiScannerService {

    private lateinit var wifiManager: WifiManager

    private lateinit var wifiScanReceiver: BroadcastReceiver

    val wifiScanStatus: MutableLiveData<WifiScanStatus> = MutableLiveData(WifiScanStatus.NO_STATUS)

    val wifiScanResultCount: MutableLiveData<Int> = MutableLiveData(null)

    fun setup(context: Context) {
        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val scanSuccess = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)

                if (scanSuccess) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
            }
        }
    }

    fun performScan(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        wifiScanStatus.postValue(WifiScanStatus.IN_PROGRESS)
        wifiScanResultCount.postValue(null)
        val scanSuccess = wifiManager.startScan()
        if (!scanSuccess) {
            scanFailure()
        }
    }

    private fun scanSuccess() {
        val result = wifiManager.scanResults

        wifiScanResultCount.postValue(result.size)

        wifiScanStatus.postValue(WifiScanStatus.LAST_SCAN_SUCCESS)
    }

    private fun scanFailure() {
        wifiScanStatus.postValue(WifiScanStatus.LAST_SCAN_FAILED)
    }
}