package gs.location_scanner.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import gs.location_scanner.data.GPSLocationStats
import gs.location_scanner.data.WifiNetworkStats
import java.lang.StringBuilder

class FileService {

    private val fileName: String = "location-data.txt"

    private lateinit var context: Context

    val locationDataPointContent: MutableLiveData<String> = MutableLiveData(null)

    fun setup(context: Context) {
        this.context = context
    }

    fun saveLocationScan(
        gpsLocationStats: GPSLocationStats?,
        wifiNetworkStats: List<WifiNetworkStats>?
    ) {
        if (gpsLocationStats != null && wifiNetworkStats != null) {
            context
                .openFileOutput(fileName, Context.MODE_APPEND)
                .use {
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
        var fileContents = StringBuilder()
        context
            .openFileInput(fileName)
            .bufferedReader()
            .use {
                it.forEachLine { line ->
                    fileContents.append(line)
                }
            }

        locationDataPointContent.postValue(fileContents.toString())
    }

    fun clearLocationsData(context: Context) {
        context.deleteFile(fileName)
        context.openFileOutput(fileName, 0).close()
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