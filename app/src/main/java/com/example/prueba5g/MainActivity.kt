package com.example.prueba5g

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.prueba5g.ui.theme.Prueba5GTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkMonitorScreen()
        }
    }
}

@Composable
fun NetworkMonitorScreen() {
    val context = LocalContext.current
    var is5GAvailable by remember { mutableStateOf(false) }
    var downSpeed by remember { mutableStateOf(0) }
    var upSpeed by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        monitorNetwork(context) { is5g, down, up ->
            is5GAvailable = is5g
            downSpeed = down
            upSpeed = up
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = if (is5GAvailable) "Red 5G disponible" else "Red 5G no disponible")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Velocidad de bajada: $downSpeed Kbps")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Velocidad de subida: $upSpeed Kbps")
        }
    }
}

fun monitorNetwork(context: Context, onNetworkChanged: (Boolean, Int, Int) -> Unit) {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return

    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return

    val is5GAvailable = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
            (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED) ||
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) ||
            (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ||
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))

    val downSpeed = capabilities.getLinkDownstreamBandwidthKbps()
    val upSpeed = capabilities.getLinkUpstreamBandwidthKbps()

    onNetworkChanged(is5GAvailable, downSpeed, upSpeed)
}
@Preview(showBackground = true)
@Composable
fun NetworkMonitorPreview() {
    Prueba5GTheme {
        NetworkMonitorPreview()
    }
}