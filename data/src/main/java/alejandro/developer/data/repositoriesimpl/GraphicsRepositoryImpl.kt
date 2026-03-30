package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.datasources.DangerZoneDataSource
import alejandro.developer.data.mappers.toDomain
import alejandro.developer.domain.models.StatsGraphicsModel
import alejandro.developer.domain.repositories.GraphicsRepository
import javax.inject.Inject

class GraphicsRepositoryImpl @Inject constructor(
    private val dataSource: DangerZoneDataSource
) : GraphicsRepository {

    override suspend fun getGraphicsStats(zoneId: Int): StatsGraphicsModel {
        return dataSource.getGraphicsStats(zoneId).toDomain()
    }

}
