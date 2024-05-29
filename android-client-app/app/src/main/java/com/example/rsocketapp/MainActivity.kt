package com.example.rsocketapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.rsocketapp.system_connection.SystemConnectionViewModel
import com.example.rsocketapp.ui.theme.RSocketAppTheme

class MainActivity : ComponentActivity() {

    private val connectivityManager: ConnectivityManager
        get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val systemConnectionViewModel: SystemConnectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RSocketAppTheme {
                MainScreen.View()
            }
        }
    }

    override fun onResume() {
        setupConnectionCallbacks()
        super.onResume()
    }

    override fun onStop() {
        disposeConnectionCallbacks()
        super.onStop()
    }

    private fun setupConnectionCallbacks() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun disposeConnectionCallbacks() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            systemConnectionViewModel.onNetworkAvailable()
        }

        override fun onLost(network: Network) {
            systemConnectionViewModel.onNetworkLost()
        }
    }
}
