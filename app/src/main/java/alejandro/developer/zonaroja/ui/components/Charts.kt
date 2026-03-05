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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EconomyBarChart(stats: EconomyStatsModel) {

    val categories = listOf(
        Triple("Renta media", stats.rentaBarrio.toFloat(), stats.rentaCiudad.toFloat()),
        Triple("Precio m²", stats.precioBarrio.toFloat(), stats.precioCiudad.toFloat())
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        verticalAlignment = Alignment.Top
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val groupWidth = size.width / categories.size
            val barWidth = groupWidth / 4
            val maxValue = categories
                .flatMap { listOf(it.second, it.third) }
                .max()


            val axisX = groupWidth / 5

            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
            }

            val textWidth = textPaint.measureText("${maxValue.toInt()}€")

            drawContext.canvas.nativeCanvas.drawText(
                "${maxValue.toInt()}€",
                axisX - textWidth / 2,
                size.height * 0.3f - 10f,
                textPaint
            )


            drawLine(
                color = Color.Black,
                start = Offset(groupWidth / 5, size.height * 0.3f),
                end = Offset(groupWidth / 5, size.height),
                strokeWidth = 2f
            )


            categories.forEachIndexed { index, category ->

                val startX = groupWidth * index + groupWidth / 5

                fun drawBar(value: Float, offset: Float, color: Color) {

                    val barHeight = size.height * 0.7f * (value / maxValue)

                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = startX + offset,
                            y = size.height - barHeight
                        ),
                        size = Size(barWidth, barHeight)
                    )


                    val textPaint = android.graphics.Paint().apply {
                        textSize = 24f
                    }
                    val textWidth = textPaint.measureText("${maxValue.toInt()}€")

                    drawContext.canvas.nativeCanvas.drawText(
                        "${value}€",
                        startX + offset + textWidth / 4,
                        size.height - barHeight - 10f,
                        textPaint
                    )
                }

                drawBar(category.second, 0f, Color(0xFFE53935))      // rojo barrio
                drawBar(category.third, barWidth * 1.5f, Color(0xFFDADADA)) // gris ciudad

            }

            drawLine(
                color = Color.Black,
                start = Offset(axisX, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )

        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

       categories.forEach { category ->

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.first,
                    textAlign = TextAlign.Center,
                    fontSize = when {
                        categories.size <= 3 -> 14.sp
                        categories.size <= 5 -> 12.sp
                        categories.size <= 7 -> 10.sp
                        else -> 8.sp
                    }
                )
            }

        }
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

@Preview(
    name = "InfoPanel - Alta peligrosidad",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun EconomyBarChart2() {

    val stastEconomy = EconomyStatsModel(
        rentaBarrio = 100,
        rentaCiudad = 1000,
        pobrezaBarrio = 50.2,
        pobrezaCiudad =  30.3,
        precioBarrio = 700,
        precioCiudad = 2400
    )


    EconomyBarChart(stastEconomy)
}