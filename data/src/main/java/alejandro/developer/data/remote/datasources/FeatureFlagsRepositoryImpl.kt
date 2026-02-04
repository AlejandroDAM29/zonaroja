package alejandro.developer.data.remote.datasources

import alejandro.developer.data.remote.general.RemoteConfigKeys
import alejandro.developer.domain.common.FeatureFlags
import alejandro.developer.domain.common.FeatureFlagsRepository
import javax.inject.Inject

class FeatureFlagsRepositoryImpl @Inject constructor(
    private val remoteConfigFirebaseFlagsDataSource: RemoteConfigFirebaseFlagsDataSource
) : FeatureFlagsRepository {

    override suspend fun getFeatureFlags(): FeatureFlags {
        remoteConfigFirebaseFlagsDataSource.fetchAndActivate()

        return FeatureFlags(
            googleLoginEnabled = remoteConfigFirebaseFlagsDataSource.getBoolean(
                RemoteConfigKeys.GOOGLE_LOGIN_ENABLED
            )
        )
    }
}
