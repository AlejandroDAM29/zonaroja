package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.HousingStatsModel
import alejandro.developer.zonaroja.ui.theme.GreaseTextFieldText
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EconomyChart(stats: EconomyStatsModel) {

    val rentaDiff =
        ((stats.rentaBarrio - stats.rentaCiudad).toFloat() / stats.rentaCiudad * 100).toInt()

    Column {

        Text(
            text = "Economía",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.size(20.dp))

        Text(
            text = if (rentaDiff < 0)
                "La renta media es ${-rentaDiff}% menor que la media de la ciudad."
            else
                "La renta media es $rentaDiff% mayor que la media de la ciudad.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.size(20.dp))

        Card(
            colors = CardColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            EconomyBarChart(stats)
        }

        Spacer(Modifier.size(12.dp))
    }
}

@Composable
fun HousingChart(stats: HousingStatsModel) {

    val maxValue = listOf(
        stats.yearBuiltBarrio.toFloat(),
        stats.yearBuiltCiudad.toFloat(),
        stats.precioBarrio.toFloat(),
        stats.precioCiudad.toFloat()
    ).max()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {

        ChartGroup(
            label = "Año medio",
            barrioValue = stats.yearBuiltBarrio.toFloat(),
            ciudadValue = stats.yearBuiltCiudad.toFloat(),
            maxValue = maxValue
        )

        ChartGroup(
            label = "Precio m²",
            barrioValue = stats.precioBarrio.toFloat(),
            ciudadValue = stats.precioCiudad.toFloat(),
            maxValue = maxValue
        )
    }
}


@Composable
fun DemographySlide(item: DemographyItemModel) {

    val animatedWidth by animateFloatAsState(
        targetValue = item.percentage,
        animationSpec = tween(900),
        label = ""
    )

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.name)
            Text("${item.percentage}%")
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(GreaseTextFieldText.copy(alpha = 0.3f), RoundedCornerShape(50))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth / 100f)
                    .height(14.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF5A5F),
                                Color(0xFFD32F2F)
                            )
                        ),
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun ChartGroup(
    label: String,
    barrioValue: Float,
    ciudadValue: Float,
    maxValue: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(verticalAlignment = Alignment.Bottom) {

            AnimatedGradientBar(
                value = barrioValue,
                maxValue = maxValue,
                colorStart = Color(0xFFFF5A5F),
                colorEnd = Color(0xFFD32F2F),
                modifier = Modifier
                    .width(28.dp)
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            AnimatedGradientBar(
                value = ciudadValue,
                maxValue = maxValue,
                colorStart = Color(0xFFE0E0E0),
                colorEnd = Color(0xFFBDBDBD),
                modifier = Modifier
                    .width(28.dp)
                    .height(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(label)
    }
}

@Composable
fun AnimatedGradientBar(
    value: Float,
    maxValue: Float,
    colorStart: Color,
    colorEnd: Color,
    modifier: Modifier = Modifier
) {

    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    Canvas(modifier = modifier) {

        val barHeight = size.height * (animatedValue / maxValue)

        val gradient = Brush.verticalGradient(
            colors = listOf(colorStart, colorEnd),
            startY = size.height - barHeight,
            endY = size.height
        )

        drawRoundRect(
            brush = gradient,
            topLeft = Offset(0f, size.height - barHeight),
            size = Size(size.width, barHeight),
            cornerRadius = CornerRadius(20f, 20f)
        )
    }
}