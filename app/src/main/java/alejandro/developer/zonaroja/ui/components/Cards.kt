package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.main.RiskLevel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.theme.Black
import alejandro.developer.zonaroja.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun LegendCard(modifier: Modifier = Modifier) {

    Card(
        modifier = modifier,
        colors = CardColors(
            containerColor = White,
            contentColor = Black,
            disabledContainerColor = White,
            disabledContentColor = Black
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            LegendItem(stringResource(R.string.high_danger_map_legend), RiskLevel.HIGH)
            LegendItem(stringResource(R.string.medium_danger_map_legend), RiskLevel.MEDIUM)
            LegendItem(stringResource(R.string.low_danger_map_legend), RiskLevel.LOW)
        }
    }
}

@Composable
fun LegendItem(
    text: String,
    level: RiskLevel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(level.toColor())
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}