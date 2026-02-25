package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.main.DangerZone
import alejandro.developer.domain.main.MapBounds
import alejandro.developer.domain.main.RiskLevel
import alejandro.developer.zonaroja.ui.theme.GreenClearMap
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.YellowClearMap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DangerMapContent(
    zones: List<DangerZone>,
    onBoundsChanged: (MapBounds) -> Unit,
    modifier: Modifier = Modifier
) {

    val sevilla = LatLng(37.3891, -5.9845)
    val cameraPositionState = rememberCameraPositionState()

    var hasLoadedInitialBounds by remember { mutableStateOf(false) }

    // 1️⃣ Centrar mapa en Sevilla al iniciar
    LaunchedEffect(Unit) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(sevilla, 12f)
        )
    }

    // 2️⃣ Primera carga automática
    LaunchedEffect(cameraPositionState.position) {
        if (!hasLoadedInitialBounds) {

            val bounds = cameraPositionState.projection
                ?.visibleRegion
                ?.latLngBounds

            bounds?.let {
                onBoundsChanged(
                    MapBounds(
                        minLat = it.southwest.latitude,
                        maxLat = it.northeast.latitude,
                        minLng = it.southwest.longitude,
                        maxLng = it.northeast.longitude
                    )
                )
                hasLoadedInitialBounds = true
            }
        }
    }

    // 3️⃣ Recargar cuando el usuario deja de mover el mapa
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving && hasLoadedInitialBounds) {

            val bounds = cameraPositionState.projection
                ?.visibleRegion
                ?.latLngBounds

            bounds?.let {
                onBoundsChanged(
                    MapBounds(
                        minLat = it.southwest.latitude,
                        maxLat = it.northeast.latitude,
                        minLng = it.southwest.longitude,
                        maxLng = it.northeast.longitude
                    )
                )
            }
        }
    }

    Box(modifier) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {

            zones.forEach { zone ->
                Polygon(
                    points = zone.points.map { LatLng(it.lat, it.lng) },
                    fillColor = zone.riskLevel.toColor(),
                    strokeColor = Color.Black,
                    strokeWidth = 2f
                )
            }
        }

        LegendCard(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Composable
fun LegendCard(modifier: Modifier = Modifier) {

    Card(
        modifier = modifier,
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            LegendItem("Alta peligrosidad", RiskLevel.HIGH)
            LegendItem("Media peligrosidad", RiskLevel.MEDIUM)
            LegendItem("Baja peligrosidad", RiskLevel.LOW)
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


fun RiskLevel.toColor(): Color {
    return when(this) {
        RiskLevel.LOW -> GreenClearMap
        RiskLevel.MEDIUM -> YellowClearMap
        RiskLevel.HIGH -> RedClearMap
    }
}