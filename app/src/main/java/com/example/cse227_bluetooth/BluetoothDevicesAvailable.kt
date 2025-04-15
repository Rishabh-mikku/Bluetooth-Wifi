package com.example.cse227_bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BluetoothDevicesAvailable : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private lateinit var scanButton: Button
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val deviceList = ArrayList<String>()

    companion object {
        private const val REQUEST_CODE_BLUETOOTH = 1001

        private val BLUETOOTH_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, // Needed for discovery
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices_available)

        listView = findViewById(R.id.listViewDevices)
        scanButton = findViewById(R.id.buttonScan)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView.adapter = arrayAdapter

        scanButton.setOnClickListener {
            if (checkPermission()) {
                discoverBluetoothDevices()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permissionsNeeded = BLUETOOTH_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_CODE_BLUETOOTH)
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoverBluetoothDevices() {
        if (!checkPermission()) return

        try {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }

            deviceList.clear()
            arrayAdapter.notifyDataSetChanged()

            bluetoothAdapter.startDiscovery()

            val filterFound = IntentFilter(BluetoothDevice.ACTION_FOUND)
            val filterFinished = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

            registerReceiver(receiver, filterFound)
            registerReceiver(receiver, filterFinished)

            Toast.makeText(this, "Scanning for devices...", Toast.LENGTH_SHORT).show()

        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        val deviceName = device.name ?: "Unknown Device"
                        val deviceHardwareAddress = device.address
                        val deviceInfo = "$deviceName\n$deviceHardwareAddress"
                        if (!deviceList.contains(deviceInfo)) {
                            deviceList.add(deviceInfo)
                            arrayAdapter.notifyDataSetChanged()
                        }
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Toast.makeText(this@BluetoothDevicesAvailable, "Discovery finished", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver already unregistered
        }

        try {
            if (checkPermission()) {
                bluetoothAdapter.cancelDiscovery()
            }
        } catch (e: SecurityException) {
            // Permission denied
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                discoverBluetoothDevices()
            } else {
                Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
