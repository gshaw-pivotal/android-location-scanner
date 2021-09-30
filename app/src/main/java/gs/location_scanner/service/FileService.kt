package gs.location_scanner.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import gs.location_scanner.data.GPSLocationStats
import gs.location_scanner.data.LocationPreview
import gs.location_scanner.data.WifiNetworkStats
import java.lang.StringBuilder

class FileService {

    private val fileName: String = "location-data.txt"

    private lateinit var context: Context

    private var dataPointCounter = 0

    private var currentLocationPreview: LocationPreview? = null

    private val locationList: MutableList<LocationPreview> = mutableListOf()

    val locationDataPointCount: MutableLiveData<Int> = MutableLiveData(null)

    val locationDataPointContent: MutableLiveData<String> = MutableLiveData(null)

    val locationPreviewList: MutableLiveData<List<LocationPreview>> = MutableLiveData(null)

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
                    buildLocationListForDisplaying(line)
                }
            }

        //The last one
        if (currentLocationPreview != null) {
            //Add the previous location object to the list
            locationList.add(currentLocationPreview!!)
        }

        locationDataPointCount.postValue(dataPointCounter)
        locationDataPointContent.postValue(fileContents.toString())
        locationPreviewList.postValue(locationList)
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
                "    \"WifiNetworkCount\": " + wifiNetworkStats.size + ",\n" +
                "    \"WifiNetworks\": [\n" +
                networkString +
                "    \n]\n" +
                "}\n"
    }

    private fun buildLocationListForDisplaying(line: String) {
        if (line.startsWith("\"LocationData\"")) {
            //Start of a new location data point\
                if (currentLocationPreview != null) {
                    //Add the previous location object to the list
                    locationList.add(currentLocationPreview!!)
                }
            currentLocationPreview = LocationPreview()
            dataPointCounter++
        }

        //Check to see if the current line has lat, long or network count
        if (line.contains("\"Latitude\"") && currentLocationPreview != null) {
            currentLocationPreview!!.latitude = getValueFromLine(line, "Latitude")
        }

        if (line.contains("\"Longitude\"") && currentLocationPreview != null) {
            currentLocationPreview!!.longitude = getValueFromLine(line, "Longitude")
        }

        if (line.contains("\"WifiNetworkCount\"") && currentLocationPreview != null) {
            currentLocationPreview!!.wifiNetworkCount = getValueFromLine(line, "WifiNetworkCount").toInt()
        }
    }

    private fun getValueFromLine(line: String, key: String): String {
        return line
            .substringAfter("\"${key}\": ")
            .replace("\"", "")
            .replace(",", "")
            .trim()
    }
}