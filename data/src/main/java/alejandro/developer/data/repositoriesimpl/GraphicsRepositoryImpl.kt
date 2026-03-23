package alejandro.developer.data.repositoriesimpl

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.requireInternet
import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.domain.models.StatsGraphicsModel
import alejandro.developer.domain.repositories.GraphicsRepository
import javax.inject.Inject

class GraphicsRepositoryImpl @Inject constructor(
    private val api: DangerZoneApi,
    private val networkMonitor: NetworkMonitor
) : GraphicsRepository {

    override suspend fun getGraphicsStats(zoneId: Int): StatsGraphicsModel {
        networkMonitor.requireInternet()
        return api.getGraphicsStats(zoneId).toDomain()
    }

}
