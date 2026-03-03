package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoPanelMap(
    zone: DangerZone,
    onClose: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = RedZoneColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = zone.zoneName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        Spacer(Modifier.height(12.dp))

        RiskBadge(zone.riskLevel)

        Spacer(Modifier.height(20.dp))

        StatsCard(zone)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* report */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedZoneColor
            )
        ) {
            Text(
                text = "Ver estadísticas",
                fontSize = 18.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { /* estadísticas */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Guardar en favoritos",
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

/*@Preview(
    name = "InfoPanel - Alta peligrosidad",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun InfoPanelMapPreviewHighRisk() {

    val fakeZone = DangerZone(
        id = 1,
        riskLevel = RiskLevel.HIGH,
        points = emptyList(),
        city = "Sevilla",
        zoneName = "Los pajaritos",
        middleIncome = 9014,
        povertyRiskRate = 57.2,
        unemploymentRate = 13.1,
        priceSquareMeter = 1000
    )

    MaterialTheme {
        InfoPanelMap(
            zone = fakeZone,
            onClose = {}
        )
    }
}*/
