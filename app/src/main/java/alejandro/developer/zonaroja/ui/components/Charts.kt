package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
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

            val dashEffect = PathEffect.dashPathEffect(
                floatArrayOf(10f, 10f), // longitud línea, longitud hueco
                0f
            )

            val chartHeight = size.height * 0.7f

            drawLine(
                color = Color.Gray,
                start = Offset(axisX, size.height - chartHeight / 2),
                end = Offset(size.width, size.height - chartHeight / 2),
                strokeWidth = 2f,
                pathEffect = dashEffect
            )

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
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color,
                                color.darker()
                            )
                        ),
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


@Composable
fun DemographyPieChart(data: List<DemographyItemModel>, colors: List<Color>) {

    Canvas(
        modifier = Modifier
            .size(220.dp)
    ) {

        var startAngle = -90f
        val gap = 2f


        data.forEachIndexed { index, item ->

            val sweepAngle = (item.percentage / 100f) * 360f - gap

            /*drawCircle(
                color = Color.Black.copy(alpha = 0.05f),
                radius = size.minDimension / 2,
                center = Offset(size.width / 2, size.height / 2 + 6f)
            )*/

            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors[index],
                        colors[index].darker()
                    )
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height),
                topLeft = Offset(0f, 0f)
            )

            startAngle += sweepAngle + gap
        }
    }

}

fun Color.darker(factor: Float = 0.75f): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)

    hsv[2] *= factor // reduce brillo

    return Color(android.graphics.Color.HSVToColor(hsv))
}