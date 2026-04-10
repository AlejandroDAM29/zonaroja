package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.zonaroja.ui.theme.badgeContainerColor
import alejandro.developer.zonaroja.ui.theme.badgeContentColor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RiskBadge(riskLevel: RiskLevel) {
    val containerColor = riskLevel.badgeContainerColor()
    val contentColor = riskLevel.badgeContentColor()
    val label = when (riskLevel) {
        RiskLevel.LOW -> "Baja peligrosidad"
        RiskLevel.MEDIUM -> "Media peligrosidad"
        RiskLevel.HIGH -> "Alta peligrosidad"
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
