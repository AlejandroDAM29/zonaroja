package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.FeatureFlagsModel

interface FeatureFlagsRepository {
    suspend fun getFeatureFlags(): FeatureFlagsModel
}