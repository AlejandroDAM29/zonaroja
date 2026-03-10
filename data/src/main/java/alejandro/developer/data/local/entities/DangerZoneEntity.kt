package alejandro.developer.data.local.entities

import alejandro.developer.domain.models.RiskLevel
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "danger_zones")
data class DangerZoneEntity(

    @PrimaryKey
    val id: Int,

    val zoneName: String,
    val city: String,

    val riskLevel: RiskLevel,

    val povertyRiskRate: Double,
    val unemploymentRate: Double,
    val priceSquareMeter: Int
)
