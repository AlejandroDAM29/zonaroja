package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.SocietyStatsModel
import alejandro.developer.domain.models.ZoneComparisonBarChartUiModel
import alejandro.developer.domain.models.ZoneComparisonMetricType
import alejandro.developer.domain.models.ZoneComparisonRiskChartUiModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ZoneComparisonRiskChartCard(
    chart: ZoneComparisonRiskChartUiModel
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = stringResource(R.string.comparison_metric_risk_level),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF221814)
            )

            chart.entries.forEach { entry ->
                val animatedRatio = animateFloatAsState(
                    targetValue = (entry.score / chart.maxScore).coerceIn(0f, 1f),
                    animationSpec = tween(durationMillis = 700),
                    label = "comparison_risk_${entry.label}"
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = entry.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF221814)
                            )
                            Text(
                                text = entry.city,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8A746D)
                            )
                        }

                        Text(
                            text = entry.riskLevel.toPresentationLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = entry.riskLevel.toRiskChartColor()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(
                                color = Color(0xFFF3ECE8),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedRatio.value)
                                .height(14.dp)
                                .background(
                                    color = entry.riskLevel.toRiskChartColor(),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ComparisonLegendItem(
                    color = RiskLevel.LOW.toRiskChartColor(),
                    label = stringResource(R.string.low)
                )
                ComparisonLegendItem(
                    color = RiskLevel.MEDIUM.toRiskChartColor(),
                    label = stringResource(R.string.medium)
                )
                ComparisonLegendItem(
                    color = RiskLevel.HIGH.toRiskChartColor(),
                    label = stringResource(R.string.high)
                )
            }
        }
    }
}

@Composable
fun ZoneComparisonMetricChartCard(
    chart: ZoneComparisonBarChartUiModel
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = chart.metricTitle(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF221814)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chart.entries.forEachIndexed { index, entry ->
                    val animatedRatio = animateFloatAsState(
                        targetValue = (entry.value / chart.maxValue).coerceIn(0f, 1f),
                        animationSpec = tween(durationMillis = 700),
                        label = "comparison_metric_${chart.metricType.name}_$index"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = entry.formattedValue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF221814),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(180.dp)
                                .background(
                                    color = Color(0xFFF3ECE8),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedRatio.value)
                                    .background(
                                        color = chart.entryColor(index),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = entry.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF221814),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = entry.city,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8A746D),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EconomyBarChart(
    zoneName: String,
    cityName: String,
    stats: EconomyStatsModel
) {

    val categories = listOf(
        Triple(
            stringResource(R.string.average_income_chart),
            stats.hoodRent.toFloat(),
            stats.cityRent.toFloat()
        ),
        Triple(
            stringResource(R.string.Price_m),
            stats.hoodPrice.toFloat(),
            stats.cityPrice.toFloat()
        )
    )

    val animatedValues = categories.map { category ->

        val hoodAnim = remember { Animatable(0f) }
        val cityAnim = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            hoodAnim.animateTo(
                category.second,
                animationSpec = tween(1000, easing = FastOutSlowInEasing)
            )

            cityAnim.animateTo(
                category.third,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            )
        }

        hoodAnim to cityAnim
    }



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
                floatArrayOf(10f, 10f),
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


            categories.forEachIndexed { index, _ ->

                val hoodAnimatedValue = animatedValues[index].first.value
                val cityAnimatedValue = animatedValues[index].second.value

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

                drawBar(hoodAnimatedValue, 0f, Color(0xFFE53935))
                drawBar(cityAnimatedValue, barWidth * 1.5f, Color(0xFFDADADA))

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

    Legend(
        zoneName = zoneName,
        cityName = cityName
    )
}

@Composable
fun Legend(
    zoneName: String,
    cityName: String
) {

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
        Text(zoneName)

        Spacer(modifier = Modifier.width(24.dp))

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFFDADADA), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(cityName)
    }
}


@Composable
fun DemographyPieChart(data: List<DemographyItemModel>, colors: List<Color>) {

    var progress by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = stringResource(R.string.label_animation_demography)
    )

    LaunchedEffect(Unit) {
        progress = 1f
    }

    Canvas(
        modifier = Modifier.size(220.dp)
    ) {

        var startAngle = -90f
        val gap = 2f

        data.forEachIndexed { index, item ->

            val sweepAngle = ((item.percentage / 100f) * 360f - gap) * animatedProgress

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
                topLeft = Offset.Zero
            )

            startAngle += (item.percentage / 100f) * 360f
        }
    }
}


@Composable
fun SocietyRadialChart(stats: SocietyStatsModel) {

    val items = listOf(
        Triple(stringResource(R.string.unemployed_population), stats.hoodUnemployment, stats.cityUnemployment),
        Triple(stringResource(R.string.risk_poverty), stats.hoodPoberty, stats.cityPoberty)
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
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
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

    hsv[2] *= factor

    return Color(android.graphics.Color.HSVToColor(hsv))
}

@Composable
fun LegendSociety() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        LegendItem(Color(0xFF2E7D32), stringResource(R.string.low))
        Spacer(Modifier.width(16.dp))

        LegendItem(Color(0xFFF9A825), stringResource(R.string.medium))
        Spacer(Modifier.width(16.dp))

        LegendItem(Color(0xFFE53935), stringResource(R.string.high))
    }
}

@Composable
fun LegendItem(color: Color, label: String) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Text(label)
    }
}


@Composable
fun RadialComparison(
    barrio: Float,
    ciudad: Float
) {

    val hoodAnim = remember { Animatable(0f) }
    val cityAnim = remember { Animatable(0f) }

    val hoodColor = getRiskColor(barrio)
    val cityColor = getRiskColor(ciudad)

    val hoodBrush = Brush.sweepGradient(
        listOf(
            hoodColor.copy(alpha = 0.7f),
            hoodColor
        )
    )

    val cityBrush = Brush.sweepGradient(
        listOf(
            cityColor.copy(alpha = 0.7f),
            cityColor
        )
    )

    LaunchedEffect(Unit) {

        cityAnim.animateTo(
            targetValue = ciudad,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )

        hoodAnim.animateTo(
            targetValue = barrio,
            animationSpec = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            val maxValue = 100f
            val strokeWidth = 18f

            val hoodSweep = hoodAnim.value / maxValue * 360f
            val citydSweep = cityAnim.value / maxValue * 360f

            drawArc(
                color = Color(0xFFEAEAEA),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth
                )
            )

            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                brush = cityBrush,
                startAngle = -90f,
                sweepAngle = citydSweep,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                brush = hoodBrush,
                startAngle = -90f,
                sweepAngle = hoodSweep,
                useCenter = false,
                style = Stroke(
                    strokeWidth,
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
                text = "${hoodAnim.value.toInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.hood_legend),
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.city_legend) + " ${cityAnim.value.toInt()}%",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ComparisonLegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6E5B55)
        )
    }
}

fun getRiskColor(value: Float): Color {
    return when {
        value < 20 -> Color(0xFF2E7D32)
        value < 30 -> Color(0xFFF9A825)
        else -> Color(0xFFE53935)
    }
}

private fun RiskLevel.toPresentationLabel(): String {
    return when (this) {
        RiskLevel.LOW -> "Bajo"
        RiskLevel.MEDIUM -> "Medio"
        RiskLevel.HIGH -> "Alto"
    }
}

private fun RiskLevel.toRiskChartColor(): Color {
    return when (this) {
        RiskLevel.LOW -> Color(0xFF2E7D32)
        RiskLevel.MEDIUM -> Color(0xFFF9A825)
        RiskLevel.HIGH -> Color(0xFFE53935)
    }
}



private fun ZoneComparisonBarChartUiModel.metricTitle(): String {
    return when (metricType) {
        ZoneComparisonMetricType.POVERTY_RISK -> "Riesgo de pobreza"
        ZoneComparisonMetricType.UNEMPLOYMENT -> "Tasa de paro"
        ZoneComparisonMetricType.PRICE_SQUARE_METER -> "Precio por metro cuadrado"
    }
}

private fun ZoneComparisonBarChartUiModel.entryColor(index: Int): Color {
    return if (index == 0) {
        RedZoneColor
    } else {
        Color(0xFF1F1F1F)
    }
}
