package gs.location_scanner.data

data class WifiNetworkStats(
    val ssid: String,
    val bssid: String,
    val signalLevelIndBm: Int,
    val signalFrequencyInMHZ: Int
)