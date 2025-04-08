package com.example.cse227_bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Bluetooth : AppCompatActivity() {
    private lateinit var lstvw : ListView
    private var aAdapter : ArrayAdapter<*>? = null
    private val bAdapter = BluetoothAdapter.getDefaultAdapter()
    companion object {
        private const val REQUEST_CODE_BLUETOOTH_CONNECT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        val btn: Button = findViewById(R.id.btnGet)
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_CODE_BLUETOOTH_CONNECT)
        }
        btn.setOnClickListener {
            if (bAdapter == null) {
                Toast.makeText(applicationContext, "Bluetooth Not Supported", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED) {
                    val pairedDevices = bAdapter.bondedDevices
                    val list = ArrayList<String>()
                    if (pairedDevices.isNotEmpty()) {
                        for (device in pairedDevices) {
                            val deviceName = device.name
                            val macAddress = device.address
                            list.add("Name: $deviceName\nMAC Address: $macAddress")
                        }
                        lstvw = findViewById(R.id.deviceList)
                        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                            list)
                        aAdapter = arrayAdapter
                        lstvw.adapter = aAdapter
                    } else {
                        Toast.makeText(applicationContext, "No Paired Devices", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Bluetooth permission is required",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH_CONNECT) {
            if ((grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Bluetooth Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}