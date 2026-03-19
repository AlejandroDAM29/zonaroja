package alejandro.developer.zonaroja.ui.components

import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.SocietyStatsModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun EconomySlide(
    zoneName: String,
    cityName: String,
    stats: EconomyStatsModel?
) {

    Column {

        Text(
            text = stringResource(R.string.economy_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.size(20.dp))

        if (stats == null) {

            EmptyStatsState(
                message = stringResource(R.string.empty_map_stat_economy)
            )

            return@Column
        }

        val rentDiff =
            ((stats.hoodRent - stats.cityRent).toFloat() / stats.cityRent * 100).toInt()

        val rentDiffDisplay = abs(rentDiff)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {

            Spacer(Modifier.size(20.dp))

            Text(
                text =  buildAnnotatedString {

                append(stringResource(R.string.average_rent_first_part)+" ")

                withStyle(style = SpanStyle(color = RedZoneColor)) {
                    append("${rentDiffDisplay}%")
                }
                    if (rentDiff < 0)
                        append(" "+stringResource(R.string.average_rent_lower_second_part))
                    else
                        append(stringResource(R.string.average_rent_higher_second_part))

            },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.size(20.dp))

            EconomyBarChart(
                zoneName = zoneName,
                cityName = cityName,
                stats = stats
            )

            Spacer(Modifier.size(12.dp))
        }
    }
}


@Composable
fun SocietySlide(stats: SocietyStatsModel?) {

    Column {

        Text(
            text = stringResource(R.string.society_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        if (stats == null) {

            EmptyStatsState(
                message = stringResource(R.string.empty_map_stat_society)
            )

            return@Column
        }

        val unemploymentDiff =
            ((stats.hoodUnemployment - stats.cityUnemployment) / stats.cityUnemployment * 100).toInt()

        val unemploymentDiffDisplay = abs(unemploymentDiff)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(20.dp)
        ) {

            Spacer(Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {

                    append(stringResource(R.string.unemployment_line_first_part)+" ")

                    withStyle(style = SpanStyle(color = RedZoneColor)) {
                        append("${unemploymentDiffDisplay}%")
                    }
                    if (unemploymentDiff > 0)
                        append( " "+ stringResource(R.string.unemployment_high_second_part))
                    else
                        append(" "+ stringResource(R.string.unemployment_low_second_part))

                },
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
            text = stringResource(R.string.demography_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        if (data.isEmpty()) {

            EmptyStatsState(
                message = stringResource(R.string.empty_map_stat_demography)
            )

            return@Column
        }

        val colors = generateChartColors(data.size)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.dist_nationality),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
