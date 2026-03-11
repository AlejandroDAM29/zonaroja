package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.screens.main.MainUiState
import alejandro.developer.zonaroja.ui.screens.main.MainViewModel
import alejandro.developer.zonaroja.ui.screens.main.StatsTab
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import alejandro.developer.zonaroja.ui.theme.White
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoPanelMap(
    uiState: MainUiState,
    onClose: () -> Unit,
    onOpenStats: () -> Unit,
    onFavoriteButtonClicked: (DangerZoneModel) -> Unit
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
                text = uiState.selectedZone!!.zoneName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        Spacer(Modifier.height(12.dp))

        RiskBadge(uiState.selectedZone!!.riskLevel)

        Spacer(Modifier.height(20.dp))

        StatsCard(uiState.selectedZone)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onOpenStats,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedZoneColor
            )
        ) {
            Text(
                text = stringResource(R.string.see_stadistics_button),
                fontSize = 18.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onFavoriteButtonClicked(uiState.selectedZone) },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!uiState.savedZonesIds.contains(uiState.selectedZone.id))
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )

            Spacer(Modifier.width(8.dp))

            Text(
                text = if (uiState.savedZonesIds.contains(uiState.selectedZone.id)) {
                    stringResource(R.string.sup_from_favourites)
                } else {
                    stringResource(R.string.save_favourites_button)
                },
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsBottomSheet(
    viewmodel: MainViewModel,
    uiState: MainUiState
) {

    if (uiState.isStatsLoading) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFFD32F2F),
                strokeWidth = 4.dp
            )
        }

    } else {

        var selectedTab by remember { mutableStateOf<StatsTab>(StatsTab.Economy) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                IconButton(
                    onClick = { viewmodel.openPanel(uiState.selectedZone!!) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.comeback_description_statisticsbottomsheet)
                    )
                }

                Text(
                    text = uiState.selectedZone!!.zoneName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = viewmodel::closeBottomSheets,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_button_statisticsbottomsheet)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StatsSegmentedControl(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )


            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                when (selectedTab) {
                    is StatsTab.Economy -> EconomySlide(uiState)
                    is StatsTab.Society -> SocietySlide(uiState.societyStats)
                    is StatsTab.Demography -> DemographySlide(uiState.demographyStats)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewmodel::closeBottomSheets,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(
                    containerColor = RedZoneColor,
                    contentColor = White,
                    disabledContainerColor = RedZoneColor,
                    disabledContentColor = White
                )
            ) {
                Text(stringResource(R.string.close_button_statisticsbottomsheet))
            }
        }
    }
}