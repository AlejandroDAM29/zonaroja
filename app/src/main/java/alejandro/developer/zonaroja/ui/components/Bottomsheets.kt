package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.HousingStatsModel
import alejandro.developer.zonaroja.ui.screens.main.MainViewModel
import alejandro.developer.zonaroja.ui.screens.main.StatsTab
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import alejandro.developer.zonaroja.ui.theme.White
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoPanelMap(
    zone: DangerZone,
    onClose: () -> Unit,
    onOpenStats: () -> Unit
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
            onClick = onOpenStats,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsBottomSheet(
    viewmodel: MainViewModel,
    zone: DangerZone
) {

    val stastEconomy = EconomyStatsModel(
        rentaBarrio = 100,
        rentaCiudad = 1000,
        pobrezaBarrio = 50.2,
        pobrezaCiudad =  30.3,
        precioBarrio = 700,
        precioCiudad = 2400
    )

    val housingStatsModel = HousingStatsModel(
        yearBuiltBarrio = 1960,
        yearBuiltCiudad = 2000,
        precioBarrio = 720,
        precioCiudad = 2000
    )

    val demographyItemModel = DemographyItemModel(
        name = "Población",
        percentage = 50.2f
    )

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
                    onClick = { viewmodel.openPanel(zone) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Text(
                    text = "Los pajaritos",
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
                        contentDescription = "Cerrar"
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
                    is StatsTab.Economy -> EconomyChart(stastEconomy)
                    is StatsTab.Housing -> HousingChart(housingStatsModel)
                    is StatsTab.Demography -> DemographySlide(demographyItemModel)
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
                Text("Cerrar")
            }
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
