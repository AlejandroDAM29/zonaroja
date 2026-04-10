package alejandro.developer.zonaroja.ui.theme

import alejandro.developer.domain.models.RiskLevel
import androidx.compose.ui.graphics.Color

fun RiskLevel.badgeContainerColor(): Color {
    return when (this) {
        RiskLevel.LOW -> Color(0xFFD9F2E3)
        RiskLevel.MEDIUM -> Color(0xFFFFE1A8)
        RiskLevel.HIGH -> Color(0xFFF8D7DA)
    }
}

fun RiskLevel.badgeContentColor(): Color {
    return when (this) {
        RiskLevel.LOW -> Color(0xFF0F5C2E)
        RiskLevel.MEDIUM -> Color(0xFF6B4100)
        RiskLevel.HIGH -> Color(0xFF8F1D1D)
    }
}
