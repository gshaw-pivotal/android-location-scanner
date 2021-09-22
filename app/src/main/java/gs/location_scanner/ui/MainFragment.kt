package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.R
import gs.location_scanner.databinding.MainFragmentBinding
import gs.location_scanner.service.GPSScannerService
import gs.location_scanner.data.ScanStatus
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

        setupInitialText()
        setupClickListeners()
        setupGPSObservers()
        setupWifiObservers()

        return fragmentBinding.root
    }

    private fun setupWifiObservers() {
        wifiScannerService.wifiScanStatus.observe(viewLifecycleOwner, {
            when (it) {
                ScanStatus.NO_STATUS -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = ""
                }
                ScanStatus.IN_PROGRESS -> {
                    wifiScanInProgress = true
                    fragmentBinding.fetchWifiDataStatus.text = getString(R.string.scanning)
                }
                ScanStatus.LAST_SCAN_SUCCESS -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = getString(R.string.scan_succeeded)
                }
                ScanStatus.LAST_SCAN_FAILED -> {
                    wifiScanInProgress = false
                    fragmentBinding.fetchWifiDataStatus.text = getString(R.string.scan_failed)
                }
            }
        })

        wifiScannerService.wifiScanResults.observe(viewLifecycleOwner, {
            wifiDetailsListAdaptor.submitList(it)
        })

        wifiScannerService.wifiScanResultCount.observe(viewLifecycleOwner, {
            if (it != null) {
                fragmentBinding.wifiCount.text = getString(
                    R.string.detected_wifi_network_count,
                    it.toString()
                )
            } else {
                fragmentBinding.wifiCount.text = getString(
                    R.string.detected_wifi_network_count,
                    getString(R.string.na)
                )
            }
        })
    }

    private fun setupGPSObservers() {
        gpsScannerService.gpsScanStatus.observe(viewLifecycleOwner, {
            when (it) {
                ScanStatus.NO_STATUS -> {
                    gpsScanInProgress = false
                    fragmentBinding.fetchGpsDataStatus.text = ""
                }
                ScanStatus.IN_PROGRESS -> {
                    gpsScanInProgress = true
                    fragmentBinding.fetchGpsDataStatus.text = getString(R.string.scanning)
                }
                ScanStatus.LAST_SCAN_SUCCESS -> {
                    gpsScanInProgress = false
                    fragmentBinding.fetchGpsDataStatus.text = getString(R.string.scan_succeeded)
                }
                ScanStatus.LAST_SCAN_FAILED -> {
                    gpsScanInProgress = false
                    fragmentBinding.fetchGpsDataStatus.text = getString(R.string.scan_failed)
                }
            }
        })

        gpsScannerService.gpsScanResults.observe(viewLifecycleOwner, {
            if (it != null) {
                fragmentBinding.gpsLatData.text = it.latitude
                fragmentBinding.gpsLongData.text = it.longitude
                fragmentBinding.gpsAltData.text = getString(R.string.na)
            } else {
                fragmentBinding.gpsLatData.text = getString(R.string.na)
                fragmentBinding.gpsLongData.text = getString(R.string.na)
                fragmentBinding.gpsAltData.text = getString(R.string.na)
            }
        })
    }

    private fun setupClickListeners() {
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
    }

    private fun setupInitialText() {
        fragmentBinding.gpsLatData.text = getString(R.string.na)
        fragmentBinding.gpsLongData.text = getString(R.string.na)
        fragmentBinding.gpsAltData.text = getString(R.string.na)

        fragmentBinding.wifiCount.text = getString(
            R.string.detected_wifi_network_count,
            getString(R.string.na)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentBinding = null
    }
}