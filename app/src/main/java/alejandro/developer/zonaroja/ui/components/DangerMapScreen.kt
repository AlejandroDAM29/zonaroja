package alejandro.developer.zonaroja.ui.components

import alejandro.developer.core.CONSTANTS.LATITUDE_INITIAL_POSITION_MAP
import alejandro.developer.core.CONSTANTS.LONGITUDE_INITIAL_POSITION_MAP
import alejandro.developer.domain.main.DangerZone
import alejandro.developer.domain.main.MapBounds
import alejandro.developer.domain.main.RiskLevel
import alejandro.developer.zonaroja.ui.theme.Black
import alejandro.developer.zonaroja.ui.theme.GreenClearMap
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.YellowClearMap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val inititalPositionMap = LatLng(LATITUDE_INITIAL_POSITION_MAP, LONGITUDE_INITIAL_POSITION_MAP)
    val cameraPositionState = rememberCameraPositionState()

    var hasLoadedInitialBounds by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(inititalPositionMap, 12f)
        )
    }

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
                    strokeColor = Black,
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


fun RiskLevel.toColor(): Color {
    return when(this) {
        RiskLevel.LOW -> GreenClearMap
        RiskLevel.MEDIUM -> YellowClearMap
        RiskLevel.HIGH -> RedClearMap
    }
}