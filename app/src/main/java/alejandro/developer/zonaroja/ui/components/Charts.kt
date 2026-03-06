package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.SocietyStatsModel
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
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun SocietyRadialChart(stats: SocietyStatsModel) {

    val items = listOf(
        Triple("Paro", stats.paroBarrio, stats.paroCiudad),
        Triple("Pobreza", stats.pobrezaBarrio, stats.pobrezaCiudad)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        items.forEach { item ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                RadialComparison(
                    barrio = item.second,
                    ciudad = item.third
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = item.first,
                    fontSize = 14.sp
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))
    LegendSociety()
}


fun Color.darker(factor: Float = 0.75f): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)

    hsv[2] *= factor // reduce brillo

    return Color(android.graphics.Color.HSVToColor(hsv))
}

@Composable
fun LegendSociety() {

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
        Text("Barrio")

        Spacer(modifier = Modifier.width(24.dp))

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFF9E9E9E), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Ciudad")
    }
}


@Composable
fun RadialComparison(
    barrio: Float,
    ciudad: Float
) {

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            val stroke = 14f
            val maxValue = 100f

            val barrioSweep = barrio / maxValue * 360f
            val ciudadSweep = ciudad / maxValue * 360f

            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(stroke)
            )

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(Color(0xFFBDBDBD), Color(0xFF757575))
                ),
                startAngle = -90f,
                sweepAngle = ciudadSweep,
                useCenter = false,
                style = Stroke(stroke)
            )

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(Color(0xFFFF5A5F), Color(0xFFD32F2F))
                ),
                startAngle = -90f,
                sweepAngle = barrioSweep,
                useCenter = false,
                style = Stroke(
                    stroke,
                    cap = StrokeCap.Round
                ),
                size = Size(size.width - 30f, size.height - 30f),
                topLeft = Offset(15f, 15f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "${barrio.toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "Barrio",
                fontSize = 10.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Ciudad ${ciudad.toInt()}%",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}