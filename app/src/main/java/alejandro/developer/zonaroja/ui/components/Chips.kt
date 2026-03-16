package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.RiskLevel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RiskBadge(riskLevel: RiskLevel) {

    val color = riskLevel.toColor()

    AssistChip(
        onClick = {},
        label = {
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = when (riskLevel) {
                    RiskLevel.LOW -> "Baja peligrosidad"
                    RiskLevel.MEDIUM -> "Media peligrosidad"
                    RiskLevel.HIGH -> "Alta peligrosidad"
                }
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        ),
        border = null
    )
}
