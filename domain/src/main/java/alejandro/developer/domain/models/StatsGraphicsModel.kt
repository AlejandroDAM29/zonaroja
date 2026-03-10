package alejandro.developer.domain.models

data class StatsGraphicsModel (
    val economy: EconomyStatsModel,
    val demography: List<DemographyItemModel>,
    val society: SocietyStatsModel
)