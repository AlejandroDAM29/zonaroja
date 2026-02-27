package alejandro.developer.data.providers

import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.repositories.FeatureFlagsRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FeatureFlagsProvider @Inject constructor(
    private val repository: FeatureFlagsRepository
) {
    private var cachedFlags: FeatureFlagsModel? = null

    suspend fun get(): FeatureFlagsModel {
        if (cachedFlags == null) {
            cachedFlags = repository.getFeatureFlags()
        }
        return cachedFlags!!
    }
}
