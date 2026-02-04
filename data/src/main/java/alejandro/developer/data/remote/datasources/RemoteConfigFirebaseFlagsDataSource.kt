package alejandro.developer.data.remote.datasources

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await

import javax.inject.Inject

class RemoteConfigFirebaseFlagsDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {

    suspend fun fetchAndActivate() {
        remoteConfig.fetchAndActivate().await()
    }

    fun getBoolean(key: String): Boolean =
        remoteConfig.getBoolean(key)
}