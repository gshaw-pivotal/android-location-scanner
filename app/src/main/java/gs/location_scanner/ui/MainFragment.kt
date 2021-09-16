package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.R
import gs.location_scanner.databinding.MainFragmentBinding
import gs.location_scanner.service.WifiScanStatus
import gs.location_scanner.service.WifiScannerService

class MainFragment : Fragment() {

    private val wifiScannerService = WifiScannerService()

    private var wifiScanInProgress: Boolean = false

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _fragmentBinding: MainFragmentBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = MainFragmentBinding.inflate(inflater, container, false)

        wifiScannerService.setup(requireContext())

        fragmentBinding.wifiCount.text = getString(R.string.detected_wifi_network_count, "N/A")

        fragmentBinding.fetchGpsData.setOnClickListener {
        }

        fragmentBinding.fetchWifiData.setOnClickListener {
            if (!wifiScanInProgress) {
                wifiScanInProgress = true
                fragmentBinding.fetchWifiDataStatus.text = ""
                wifiScannerService.performScan(requireContext())
            }
        }

        fragmentBinding.saveDataPoint.setOnClickListener {
        }

        wifiScannerService.wifiScanStatus.observe(viewLifecycleOwner, {
            when (it) {
                WifiScanStatus.NO_STATUS -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = ""
                }
                WifiScanStatus.IN_PROGRESS -> {
                    wifiScanInProgress = true
                    fragmentBinding.fetchWifiDataStatus.text = "Scanning"
                }
                WifiScanStatus.LAST_SCAN_SUCCESS -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = "Scan Succeeded"
                }
                WifiScanStatus.LAST_SCAN_FAILED -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = "Scan Failed"
                }
            }
        })

        wifiScannerService.wifiScanResultCount.observe(viewLifecycleOwner, {
            if (it != null) {
                fragmentBinding.wifiCount.text = getString(R.string.detected_wifi_network_count, it.toString())
            } else {
                fragmentBinding.wifiCount.text = getString(R.string.detected_wifi_network_count, "N/A")
            }
        })

        return fragmentBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentBinding = null
    }
}