package com.example.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var tvStatus: TextView
    private lateinit var btnCheck: Button
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {

            runOnUiThread { updateStatus() }
        }

        override fun onLost(network: Network) {

            runOnUiThread { updateStatus() }
        }
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {

            runOnUiThread { updateStatus() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnCheck = findViewById(R.id.btnCheck)

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        btnCheck.setOnClickListener {
            val connected = isConnected()
            val type = getConnectionType()
            Toast.makeText(
                this,
                if (connected) "Connecté ($type)" else "Non connecté",
                Toast.LENGTH_SHORT
            ).show()
            updateStatus()
        }
    }
    override fun onStart() {
        super.onStart()

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        updateStatus()
    }
    override fun onStop() {
        super.onStop()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {

        }
    }
    private fun isConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun getConnectionType(): String {
        val activeNetwork = connectivityManager.activeNetwork ?: return "Aucun"
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "Inconnu"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Autre"
        }
    }
    private fun updateStatus() {
        val connected = isConnected()
        val type = getConnectionType()
        tvStatus.text = if (connected) {
            "État du réseau : connecté ($type)"
        } else {
            "État du réseau : non connecté"
        }
    }
}