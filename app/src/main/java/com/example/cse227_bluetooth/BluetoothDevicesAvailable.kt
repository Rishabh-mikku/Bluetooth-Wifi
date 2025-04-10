package com.example.cse227_bluetooth

import android.Manifest
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
        val permissionNeeded = BLUETOOTH_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionNeeded.isNotEmpty()) {
            if (permissionNeeded.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
                Toast.makeText(this, "Bluetooth permissions are required to scan for devices", Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(this, permissionNeeded.toTypedArray(), REQUEST_CODE_BLUETOOTH)
            return false
        }
        return true
    }

    private fun discoverBluetoothDevices() {
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
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name ?: "Unknown Device"
                    val deviceHardwareAddress = device?.address // MAC address
                    val deviceInfo = "$deviceName\n$deviceHardwareAddress"
                    if (!deviceList.contains(deviceInfo)) {
                        deviceList.add(deviceInfo)
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Toast.makeText(this@BluetoothDevicesAvailable, "Discovery finished", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver already unregistered
        }
        bluetoothAdapter.cancelDiscovery()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                discoverBluetoothDevices()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
