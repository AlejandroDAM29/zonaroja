package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.main.DangerZone
import alejandro.developer.domain.main.MapBounds
import alejandro.developer.domain.main.RiskLevel
import alejandro.developer.zonaroja.ui.theme.GreenClearMap
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.YellowClearMap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DangerMapContent(
    zones: List<DangerZone>,
    onBoundsChanged: (MapBounds) -> Unit
) {

    val sevilla = LatLng(37.3891, -5.9845)

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(sevilla, 12f)
        )
    }

    Box(Modifier.fillMaxSize()) {

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

        /*if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }*/
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {

            val bounds = cameraPositionState.projection
                ?.visibleRegion
                ?.latLngBounds

            bounds?.let {
                onBoundsChanged(
                    MapBounds(
                        it.southwest.latitude,
                        it.northeast.latitude,
                        it.southwest.longitude,
                        it.northeast.longitude
                    )
                )
            }
        }
    }
}

fun RiskLevel.toColor(): Color {
    return when(this) {
        RiskLevel.LOW -> GreenClearMap
        RiskLevel.MEDIUM -> YellowClearMap
        RiskLevel.HIGH -> RedClearMap
    }
}