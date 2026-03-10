package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.StatsGraphicsModel
import alejandro.developer.domain.repositories.GraphicsRepository
import javax.inject.Inject

class GetGraphicsStatsUseCase @Inject constructor(
    private val repository: GraphicsRepository
) {

    suspend operator fun invoke(zoneId: Int): StatsGraphicsModel {
        return repository.getGraphicsStats(zoneId)
    }

}