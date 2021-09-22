package gs.location_scanner.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.lifecycle.MutableLiveData
import gs.location_scanner.data.ScanStatus
import gs.location_scanner.data.WifiNetworkStats

class WifiScannerService {

    private lateinit var wifiManager: WifiManager

    private lateinit var wifiScanReceiver: BroadcastReceiver

    val wifiScanStatus: MutableLiveData<ScanStatus> = MutableLiveData(ScanStatus.NO_STATUS)

    val wifiScanResultCount: MutableLiveData<Int> = MutableLiveData(null)

    val wifiScanResults: MutableLiveData<List<WifiNetworkStats>> = MutableLiveData(null)

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

        wifiScanStatus.postValue(ScanStatus.IN_PROGRESS)
        wifiScanResults.postValue(null)
        wifiScanResultCount.postValue(null)
        val scanSuccess = wifiManager.startScan()
        if (!scanSuccess) {
            scanFailure()
        }
    }

    private fun scanSuccess() {
        val result = wifiManager.scanResults

        val wifiNetworkStatList = result.map {
                network -> WifiNetworkStats(
                                network.SSID,
                                network.BSSID,
                                network.level,
                                network.frequency
                            )
        }

        wifiScanResultCount.postValue(result.size)
        wifiScanResults.postValue(wifiNetworkStatList)
        wifiScanStatus.postValue(ScanStatus.LAST_SCAN_SUCCESS)
    }

    private fun scanFailure() {
        wifiScanStatus.postValue(ScanStatus.LAST_SCAN_FAILED)
    }
}