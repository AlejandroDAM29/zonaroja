package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.SocietyStatsModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EconomyChart(stats: EconomyStatsModel?) {

    Column {

        Text(
            text = "Economía",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.size(20.dp))

        if (stats == null) {

            EmptyStatsState(
                message = "No hay datos económicos disponibles para esta zona."
            )

            return@Column
        }

        val rentaDiff =
            ((stats.rentaBarrio - stats.rentaCiudad).toFloat() / stats.rentaCiudad * 100).toInt()

        Card(
            colors = CardColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Spacer(Modifier.size(20.dp))

            Text(
                text = if (rentaDiff < 0)
                    "La renta media es ${-rentaDiff}% menor que la media de la ciudad."
                else
                    "La renta media es $rentaDiff% mayor que la media de la ciudad.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.size(20.dp))

            EconomyBarChart(stats)

            Spacer(Modifier.size(12.dp))
        }
    }
}


@Composable
fun SocietySlide(stats: SocietyStatsModel?) {

    Column {

        Text(
            text = "Sociedad",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        if (stats == null) {

            EmptyStatsState(
                message = "No hay datos sociales disponibles para esta zona."
            )

            return@Column
        }

        val paroDiff =
            ((stats.paroBarrio - stats.paroCiudad) / stats.paroCiudad * 100).toInt()

        Card(
            colors = CardColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black
            ),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(20.dp)
        ) {

            Spacer(Modifier.height(20.dp))

            Text(
                text = if (paroDiff > 0)
                    "El paro es $paroDiff% mayor que la media de la ciudad."
                else
                    "El paro es ${-paroDiff}% menor que la media de la ciudad.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(20.dp))

            SocietyRadialChart(stats)

            Spacer(Modifier.height(20.dp))
        }
    }
}


@Composable
fun DemographySlide(data: List<DemographyItemModel>) {

    Column {

        Text(
            text = "Demografía",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        if (data.isEmpty()) {

            EmptyStatsState(
                message = "No hay datos demográficos disponibles para esta zona."
            )

            return@Column
        }

        val colors = generateChartColors(data.size)

        Card(
            colors = CardColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black,
            ),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Distribución de nacionalidades en la zona",
                    style = MaterialTheme.typography.bodyLarge
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DemographyPieChart(data, colors)
                }

                Spacer(Modifier.height(20.dp))

                data.forEachIndexed { index, item ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color = colors[index % colors.size], CircleShape)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "${item.name} ${item.percentage}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (index != data.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            thickness = 0.5.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun EmptyStatsState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

fun generateChartColors(count: Int): List<Color> {
    return List(count) { index ->
        val hue = (index * 360f / count)
        Color.hsv(hue, 0.7f, 0.9f)
    }
}