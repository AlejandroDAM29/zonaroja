package alejandro.developer.data.local.entities

import alejandro.developer.domain.models.RiskLevel
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "danger_zones",
    indices = [
        Index(value = ["zoneId", "userId"], unique = true),
        Index(value = ["userId"])
    ]
)
data class DangerZoneEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,

    val zoneId: Int,
    val userId: String,

    val zoneName: String,
    val city: String,

    val riskLevel: RiskLevel,

    val povertyRiskRate: Double,
    val unemploymentRate: Double,
    val priceSquareMeter: Int
)
