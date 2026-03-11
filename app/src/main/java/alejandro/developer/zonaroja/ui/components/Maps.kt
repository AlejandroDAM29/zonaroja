package alejandro.developer.zonaroja.ui.components

import alejandro.developer.core.CONSTANTS.LATITUDE_INITIAL_POSITION_MAP
import alejandro.developer.core.CONSTANTS.LONGITUDE_INITIAL_POSITION_MAP
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.theme.Black
import alejandro.developer.zonaroja.ui.theme.GreenClearMap
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.YellowClearMap
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DangerMapContent(
    isSearcherNameSpacerExpanded: Boolean,
    zones: List<DangerZoneModel>,
    savedZones: List<Int>,
    searchQuery: String,
    onBoundsChanged: (MapBounds) -> Unit,
    modifier: Modifier = Modifier,
    searchedLocation: LatLng?,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onSearchConsumed: () -> Unit,
    onExpandHideClick: () -> Unit,
    onOpenPanel: (DangerZoneModel) -> Unit
) {

    val inititalPositionMap = LatLng(LATITUDE_INITIAL_POSITION_MAP, LONGITUDE_INITIAL_POSITION_MAP)
    val cameraPositionState = rememberCameraPositionState()
    var currentZoom by remember { mutableFloatStateOf(cameraPositionState.position.zoom) }
    val zoomBucket = when {
        currentZoom < 13f -> 0
        currentZoom < 15f -> 1
        else -> 2
    }

    var hasLoadedInitialBounds by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(inititalPositionMap, 12f)
        )
        //Obtain ids saved from bbdd
        /*obtainSavedZoneIds()*/
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position.zoom }
            .collect { newZoom ->
                currentZoom = newZoom
            }
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
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                maxZoomPreference = 16f,
                minZoomPreference = 12f
            )
        ) {

            zones.forEach { zone ->
                Polygon(
                    clickable = true,
                    points = zone.points.map { LatLng(it.lat, it.lng) },
                    fillColor = zone.riskLevel.toColor(),
                    strokeColor = Black,
                    strokeWidth = 2f,
                    onClick = { onOpenPanel(zone) }
                )

                val center = polygonCenter(zone.points)

                key(zoomBucket) {
                    if (savedZones.contains(zone.id)) {
                        MarkerComposable(
                            state = remember { MarkerState(position = center) },
                            anchor = Offset(0.5f, 0.5f)
                        ) {

                            Log.i("test-100", "zoom: $currentZoom");
                            val iconSize = when {
                                currentZoom < 13f -> 12.dp
                                currentZoom < 15f -> 20.dp
                                currentZoom < 16f -> 24.dp
                                else -> 28.dp
                            }

                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = "Zona favorita",
                                tint = Color.Blue,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    }
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
                        placeholder = { Text(stringResource(R.string.look_for_city)) },
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

fun polygonCenter(points: List<GeoPoint>): LatLng {

    var area = 0.0
    var centroidLat = 0.0
    var centroidLng = 0.0

    for (i in points.indices) {

        val p1 = points[i]
        val p2 = points[(i + 1) % points.size]

        val factor = (p1.lat * p2.lng - p2.lat * p1.lng)

        area += factor
        centroidLat += (p1.lat + p2.lat) * factor
        centroidLng += (p1.lng + p2.lng) * factor
    }

    area *= 0.5

    centroidLat /= (6 * area)
    centroidLng /= (6 * area)

    return LatLng(centroidLat, centroidLng)
}