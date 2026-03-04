package alejandro.developer.zonaroja.ui.components

import alejandro.developer.core.CONSTANTS.LATITUDE_INITIAL_POSITION_MAP
import alejandro.developer.core.CONSTANTS.LONGITUDE_INITIAL_POSITION_MAP
import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.zonaroja.ui.theme.Black
import alejandro.developer.zonaroja.ui.theme.GreenClearMap
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.YellowClearMap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
    isSearcherNameSpacerExpanded: Boolean,
    zones: List<DangerZone>,
    searchQuery: String,
    onBoundsChanged: (MapBounds) -> Unit,
    modifier: Modifier = Modifier,
    searchedLocation: LatLng?,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onSearchConsumed: () -> Unit,
    onExpandHideClick: () -> Unit,
    onOpenPanel: (DangerZone) -> Unit
) {

    val inititalPositionMap = LatLng(LATITUDE_INITIAL_POSITION_MAP, LONGITUDE_INITIAL_POSITION_MAP)
    val cameraPositionState = rememberCameraPositionState()
    val currentZoom = cameraPositionState.position.zoom
    val minZoomToShowZones = 12f
    val shouldShowZones = currentZoom >= minZoomToShowZones

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

    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 12f)
            )
        }
    }

    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 12f)
            )
            onSearchConsumed()   // 👈 nuevo callback
        }
    }

    Box(modifier) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            if (shouldShowZones) {
                zones.forEach { zone ->
                    Polygon(
                        clickable = true,
                        points = zone.points.map { LatLng(it.lat, it.lng) },
                        fillColor = zone.riskLevel.toColor(),
                        strokeColor = Black,
                        strokeWidth = 2f,
                        onClick = { onOpenPanel(zone) }
                    )
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {

            val targetWidth = if (isSearcherNameSpacerExpanded) {
                maxWidth
            } else {
                48.dp
            }

            val animatedWidth by animateDpAsState(
                targetValue = targetWidth,
                animationSpec = tween(300),
                label = ""
            )

            Box(
                modifier = Modifier
                    .height(56.dp)
                    .width(animatedWidth)
                    .background(Color.White)
            ) {

                if (isSearcherNameSpacerExpanded) {

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = { Text("Buscar ciudad...") },
                        singleLine = true,
                        leadingIcon = {
                            IconButton(onClick = onExpandHideClick) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = onSearchTriggered) {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                } else {

                    IconButton(
                        onClick = onExpandHideClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            }
        }

        if (!shouldShowZones) {
            AnimatedVisibility(
                visible = !shouldShowZones,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Acércate más para ver los barrios",
                        color = Color.White
                    )
                }
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
    return when (this) {
        RiskLevel.LOW -> GreenClearMap
        RiskLevel.MEDIUM -> YellowClearMap
        RiskLevel.HIGH -> RedClearMap
    }
}