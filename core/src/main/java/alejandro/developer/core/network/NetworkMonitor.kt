package alejandro.developer.core.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline: Flow<Boolean>

    fun isCurrentlyOnline(): Boolean
}
