package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.EconomyStatsModel
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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

    val maxValue = listOf(
        stats.rentaBarrio,
        stats.rentaCiudad,
        stats.precioBarrio,
        stats.precioCiudad,
        stats.pobrezaBarrio.toInt(),
        stats.pobrezaCiudad.toInt()
    ).max()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {

        val barWidth = size.width / 8
        val spacing = barWidth / 2

        fun drawBar(
            value: Float,
            index: Int,
            color: Color
        ) {
            val heightRatio = value / maxValue
            val barHeight = size.height * heightRatio

            drawRect(
                color = color,
                topLeft = Offset(
                    x = spacing + index * (barWidth + spacing),
                    y = size.height - barHeight
                ),
                size = Size(barWidth, barHeight)
            )
        }

        drawBar(stats.rentaBarrio.toFloat(), 0, Color.Red)
        drawBar(stats.rentaCiudad.toFloat(), 1, Color.LightGray)

        drawBar(stats.pobrezaBarrio.toFloat(), 2, Color.Red)
        drawBar(stats.pobrezaCiudad.toFloat(), 3, Color.LightGray)

        drawBar(stats.precioBarrio.toFloat(), 4, Color.Red)
        drawBar(stats.precioCiudad.toFloat(), 5, Color.LightGray)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Legend()
}

@Composable
fun Legend() {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color.Red, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Barrio")

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color.LightGray, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Ciudad")
    }
}