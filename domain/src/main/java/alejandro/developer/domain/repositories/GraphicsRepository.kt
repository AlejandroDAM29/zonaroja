package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.StatsGraphicsModel

interface GraphicsRepository {
    suspend fun getGraphicsStats(zoneId: String): StatsGraphicsModel
}