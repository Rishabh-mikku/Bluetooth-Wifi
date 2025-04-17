package com.example.cse227_bluetooth

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class WifiActivity : AppCompatActivity() {
    private lateinit var lstview: ListView
    private var aAdapter: ArrayAdapter<String>? = null
    private lateinit var wifiManager: WifiManager
    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)
        // Initialize Wi-Fi Manager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val btn = findViewById<Button>(R.id.btnScanWifi)
        lstview = findViewById(R.id.wifiListView)
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION)
        }
        btn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                if (!wifiManager.isWifiEnabled) {
                    Toast.makeText(this, "Enabling Wi-Fi...", Toast.LENGTH_SHORT).show()
                    wifiManager.isWifiEnabled = true
                }
                wifiManager.startScan()
                val scanResults = wifiManager.scanResults
                displayWifiNetworks(scanResults)
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun displayWifiNetworks(scanResults: List<ScanResult>) {
        val list = ArrayList<String>()
        if (scanResults.isNotEmpty()) {
            for (result in scanResults) {
                val ssid = result.SSID
                val bssid = result.BSSID
                val signalLevel = WifiManager.calculateSignalLevel(result.level, 5)
                list.add("SSID: $ssid\nBSSID: $bssid\nSignal Level: $signalLevel")
            }
            aAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
            lstview.adapter = aAdapter
        } else {
            Toast.makeText(this, "No Wi-Fi networks found", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location Permission is required for Wi-Fi",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}