package alejandro.developer.data.datasources

import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.domain.models.MapBounds
import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class ModsDangerZoneDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    moshi: Moshi
) : DangerZoneDataSource {

    private val dangerZoneListAdapter: JsonAdapter<List<DangerZoneDto>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, DangerZoneDto::class.java)
    )

    private val zoneStatsListAdapter: JsonAdapter<List<ModZoneStatsEntry>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, ModZoneStatsEntry::class.java)
    )

    private val dangerZones by lazy {
        readAsset(
            path = DANGER_ZONES_ASSET_PATH,
            adapter = dangerZoneListAdapter
        )
    }

    private val zoneStatsEntries by lazy {
        readAsset(
            path = GRAPHICS_STATS_ASSET_PATH,
            adapter = zoneStatsListAdapter
        )
    }

    override suspend fun getDangerZonesForComparison(): List<DangerZoneComparisonDto> {
        return dangerZones.map { it.toComparisonDto() }
    }

    override suspend fun getDangerZones(bounds: MapBounds): List<DangerZoneDto> {
        return dangerZones.filter { it.intersects(bounds) }
    }

    override suspend fun getGraphicsStats(zoneId: Int): StatsGraphicsDto {
        return zoneStatsEntries
            .firstOrNull { it.zoneId == zoneId }
            ?.stats
            ?: error("No mod graphics stats found for zoneId=$zoneId")
    }

    private fun <T> readAsset(
        path: String,
        adapter: JsonAdapter<T>
    ): T {
        val json = context.assets.open(path).bufferedReader().use { it.readText() }
        return requireNotNull(adapter.fromJson(json)) {
            "Unable to parse mod asset at $path"
        }
    }

    private fun DangerZoneDto.toComparisonDto(): DangerZoneComparisonDto {
        return DangerZoneComparisonDto(
            id = id,
            zoneName = zoneName,
            city = city,
            riskLevel = riskLevel,
            povertyRiskRate = povertyRiskRate,
            unemploymentRate = unemploymentRate,
            priceSquareMeter = priceSquareMeter
        )
    }

    private fun DangerZoneDto.intersects(bounds: MapBounds): Boolean {
        if (points.isEmpty()) return false

        val zoneMinLat = points.minOf { it.lat }
        val zoneMaxLat = points.maxOf { it.lat }
        val zoneMinLng = points.minOf { it.lng }
        val zoneMaxLng = points.maxOf { it.lng }

        return zoneMaxLat >= bounds.minLat &&
            zoneMinLat <= bounds.maxLat &&
            zoneMaxLng >= bounds.minLng &&
            zoneMinLng <= bounds.maxLng
    }
    private companion object {
        const val DANGER_ZONES_ASSET_PATH = "mods/danger_zones.json"
        const val GRAPHICS_STATS_ASSET_PATH = "mods/graphics_stats.json"
    }
}

private data class ModZoneStatsEntry(
    val zoneId: Int,
    val stats: StatsGraphicsDto
)
