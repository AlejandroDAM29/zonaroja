package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.ui.screens.main.StatsTab
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatsTabRow(
    selectedTab: StatsTab?,
    onTabSelected: (StatsTab) -> Unit
) {

    val tabs = listOf(
        StatsTab.Economy,
        StatsTab.Housing,
        StatsTab.Demography
    )

    val safeTab = selectedTab ?: StatsTab.Economy

    val selectedIndex = tabs.indexOfFirst { it == safeTab }
        .let { if (it == -1) 0 else it }

    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth(),
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = RedZoneColor
            )
        },
        tabs = {
            tabs.forEachIndexed { index, tab ->

                Tab(
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.height(56.dp),
                    text = {
                        Text(
                            text = tab.title,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    )
}