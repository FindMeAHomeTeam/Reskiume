package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeKonnectivity(
    override val currentNetworkConnection: NetworkConnection = NetworkConnection.WIFI,
    override val currentNetworkConnectionState: StateFlow<NetworkConnection> = MutableStateFlow(NetworkConnection.WIFI),
    override val isConnected: Boolean = true,
    override val isConnectedState: StateFlow<Boolean> = MutableStateFlow(true)
) : Konnectivity
