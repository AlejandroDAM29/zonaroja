package alejandro.developer.data.remote.datasources

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.data.remote.general.RemoteConfigKeys
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.repositories.FeatureFlagsRepository
import javax.inject.Inject

class FeatureFlagsRepositoryImpl @Inject constructor(
    private val remoteConfigFirebaseFlagsDataSource: RemoteConfigFirebaseFlagsDataSource,
    private val networkMonitor: NetworkMonitor
) : FeatureFlagsRepository {

    override suspend fun getFeatureFlags(): FeatureFlagsModel {
        if (networkMonitor.isCurrentlyOnline()) {
            runCatching {
                remoteConfigFirebaseFlagsDataSource.fetchAndActivate()
            }
        }

        return FeatureFlagsModel(
            googleLoginEnabled = remoteConfigFirebaseFlagsDataSource.getBoolean(
                RemoteConfigKeys.GOOGLE_LOGIN_ENABLED
            )
        )
    }
}
