package alejandro.developer.domain.common

interface FeatureFlagsRepository {
    suspend fun getFeatureFlags(): FeatureFlags
}