package gs.location_scanner.service

import android.content.Context
import gs.location_scanner.data.GPSLocationStats
import gs.location_scanner.data.WifiNetworkStats
import java.io.FileInputStream
import java.io.FileOutputStream

class FileService {

    private lateinit var fileWriter: FileOutputStream

    private lateinit var fileReader: FileInputStream

    fun setup(context: Context) {
        fileWriter = context.openFileOutput("location-data.txt", Context.MODE_APPEND)
        fileReader = context.openFileInput("location-data.txt")
    }

    fun saveLocationScan(
        gpsLocationStats: GPSLocationStats?,
        wifiNetworkStats: List<WifiNetworkStats>?
    ) {
        if (gpsLocationStats != null && wifiNetworkStats != null) {
            fileWriter.use {
                it.write(
                    generateLocationScanString(
                        gpsLocationStats,
                        wifiNetworkStats
                    ).toByteArray()
                )
            }
        }
    }

    fun viewLocationsData() {
        fileReader.bufferedReader().use {
            it.forEachLine { line -> println(line) }
        }
    }

    private fun generateLocationScanString(
        gpsLocationStats: GPSLocationStats,
        wifiNetworkStats: List<WifiNetworkStats>
    ): String {
        val locationString = "\"Location\": {\n" +
                "      \"Latitude\": \"" + gpsLocationStats.latitude + "\",\n" +
                "      \"Longitude\": \"" + gpsLocationStats.longitude + "\",\n" +
                "      \"Altitude\": \"N/A\"\n" +
                "    }"

        val networkString = if (wifiNetworkStats.isNotEmpty()) {
            wifiNetworkStats.joinToString(separator = ",") { network ->
                "{" +
                "        \"Network\": {\n" +
                "          \"SSID\": \"" + network.ssid + "\",\n" +
                "          \"BSSID\": \"" + network.bssid + "\",\n" +
                "          \"SignalStrength\": " + network.signalLevelIndBm + ",\n" +
                "          \"SignalFrequency\": " + network.signalFrequencyInMHZ + "\n" +
                "        }\n" +
                "}"
            }
        } else {
            ""
        }

        return "\"LocationData\": {\n" +
        "    " + locationString + ",\n" +
        "    \"WifiNetworks\": [\n" +
                networkString +
        "    \n]\n" +
        "}"
    }
}