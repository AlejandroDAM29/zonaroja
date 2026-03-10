package alejandro.developer.data.local.converters

import alejandro.developer.domain.models.RiskLevel
import androidx.room.TypeConverter

class RiskLevelConverter {

    @TypeConverter
    fun fromRiskLevel(level: RiskLevel): String {
        return level.name
    }

    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel {
        return RiskLevel.valueOf(value)
    }
}