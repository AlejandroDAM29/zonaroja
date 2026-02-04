package alejandro.developer.data.providers

import alejandro.developer.domain.common.FeatureFlags
import alejandro.developer.domain.common.FeatureFlagsRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FeatureFlagsProvider @Inject constructor(
    private val repository: FeatureFlagsRepository
) {
    private var cachedFlags: FeatureFlags? = null

    suspend fun get(): FeatureFlags {
        if (cachedFlags == null) {
            cachedFlags = repository.getFeatureFlags()
        }
        return cachedFlags!!
    }
}
