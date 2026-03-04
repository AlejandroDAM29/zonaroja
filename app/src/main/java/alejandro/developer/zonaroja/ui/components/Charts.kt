package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.EconomyStatsModel
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

@Composable
fun EconomyBarChart(stats: EconomyStatsModel) {

    val categories = listOf(
        Triple("Renta media", stats.rentaBarrio.toFloat(), stats.rentaCiudad.toFloat()),
        Triple("Tasa pobreza", stats.pobrezaBarrio.toFloat(), stats.pobrezaCiudad.toFloat()),
        Triple("Precio m²", stats.precioBarrio.toFloat(), stats.precioCiudad.toFloat())
    )

    val maxValue = categories.flatMap { listOf(it.second, it.third) }.max()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {

        val groupWidth = size.width / categories.size
        val barWidth = groupWidth / 4

        categories.forEachIndexed { index, category ->

            val startX = groupWidth * index + groupWidth / 4

            fun drawBar(value: Float, offset: Float, color: Color) {

                val heightRatio = value / maxValue
                val barHeight = size.height * 0.7f * heightRatio

                drawRect(
                    color = color,
                    topLeft = Offset(
                        x = startX + offset,
                        y = size.height - barHeight
                    ),
                    size = Size(barWidth, barHeight)
                )
            }

            drawBar(category.second, 0f, Color(0xFFE53935))      // rojo barrio
            drawBar(category.third, barWidth * 1.5f, Color(0xFFDADADA)) // gris ciudad
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Text("Renta media")
        Text("Tasa pobreza")
        Text("Precio m²")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Legend()
}

@Composable
fun Legend() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFFE53935), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Los pajaritos")

        Spacer(modifier = Modifier.width(24.dp))

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFFDADADA), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Media Sevilla")
    }
}