package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.R
import gs.location_scanner.databinding.MainFragmentBinding
import gs.location_scanner.service.GPSScannerService
import gs.location_scanner.service.ScanStatus
import gs.location_scanner.service.WifiScannerService

class MainFragment : Fragment() {

    private val wifiDetailsListAdaptor = WifiDetailsListAdapter()

    private val wifiScannerService = WifiScannerService()

    private val gpsScannerService = GPSScannerService()

    private var wifiScanInProgress: Boolean = false

    private var gpsScanInProgress: Boolean = false

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

        fragmentBinding.wifiDetailsList.adapter = wifiDetailsListAdaptor

        wifiScannerService.setup(requireContext())

        gpsScannerService.setup(requireContext())

        fragmentBinding.gpsLatData.text = "N/A"
        fragmentBinding.gpsLongData.text = "N/A"
        fragmentBinding.gpsAltData.text = "N/A"

        fragmentBinding.wifiCount.text = getString(R.string.detected_wifi_network_count, "N/A")

        fragmentBinding.fetchGpsData.setOnClickListener {
            if (!gpsScanInProgress) {
                gpsScanInProgress = true
                fragmentBinding.fetchGpsDataStatus.text = ""
                gpsScannerService.getLocation(requireContext())
            }
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

        gpsScannerService.gpsScanStatus.observe(viewLifecycleOwner, {
            when (it) {
                ScanStatus.NO_STATUS -> {
                    gpsScanInProgress = false
                    fragmentBinding.fetchGpsDataStatus.text = ""
                }
                ScanStatus.IN_PROGRESS -> {
                    gpsScanInProgress = true
                    fragmentBinding.fetchGpsDataStatus.text = "Scanning"
                }
                ScanStatus.LAST_SCAN_SUCCESS -> {
                    gpsScanInProgress = false
                    fragmentBinding.fetchGpsDataStatus.text = "Scan Succeeded"
                }
                ScanStatus.LAST_SCAN_FAILED -> {
                    gpsScanInProgress = false
                    fragmentBinding.fetchGpsDataStatus.text = "Scan Failed"
                }
            }
        })

        gpsScannerService.gpsScanResults.observe(viewLifecycleOwner, {
            if (it != null) {
                fragmentBinding.gpsLatData.text = it.latitude
                fragmentBinding.gpsLongData.text = it.longitude
                fragmentBinding.gpsAltData.text = "N/A"
            } else {
                fragmentBinding.gpsLatData.text = "N/A"
                fragmentBinding.gpsLongData.text = "N/A"
                fragmentBinding.gpsAltData.text = "N/A"
            }
        })

        wifiScannerService.wifiScanStatus.observe(viewLifecycleOwner, {
            when (it) {
                ScanStatus.NO_STATUS -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = ""
                }
                ScanStatus.IN_PROGRESS -> {
                    wifiScanInProgress = true
                    fragmentBinding.fetchWifiDataStatus.text = "Scanning"
                }
                ScanStatus.LAST_SCAN_SUCCESS -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = "Scan Succeeded"
                }
                ScanStatus.LAST_SCAN_FAILED -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = "Scan Failed"
                }
            }
        })

        wifiScannerService.wifiScanResults.observe(viewLifecycleOwner, {
            wifiDetailsListAdaptor.submitList(it)
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