package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.ui.screens.main.StatsTab
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatsSegmentedControl(
    selectedTab: StatsTab,
    onTabSelected: (StatsTab) -> Unit
) {

    val tabs = listOf(
        StatsTab.Economy,
        StatsTab.Housing,
        StatsTab.Demography
    )

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {

        tabs.forEachIndexed { index, tab ->

            SegmentedButton(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = tabs.size
                ),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = RedZoneColor,
                    activeContentColor = Color.White,
                    inactiveContainerColor = Color(0xFFE8E5EA),
                    inactiveContentColor = Color.Gray
                ),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 4.dp
                )
            ) {

                Text(
                    text = tab.title,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}