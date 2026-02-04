package alejandro.developer.zonaroja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ZonaRojaTitle(
    modifier: Modifier = Modifier,
    text1: String,
    text2: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val commonStyle = TextStyle(
            fontSize = 66.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default,
            letterSpacing = (-1.2).sp,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 1f),
                offset = Offset(0f, 6f),
                blurRadius = 24f
            )
        )

        Text(
            text = "$text1 ",
            color = Color.White.copy(alpha = 0.70f),
            style = commonStyle
        )

        Text(
            text = text2,
            color = Color(0xFFD32F2F).copy(alpha = 0.70f),
            style = commonStyle
        )
    }
}



@Composable
fun OrDivider(
    modifier: Modifier = Modifier,
    text: String = "O"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.6f),
            thickness = 1.dp
        )

        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp),
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.6f),
            thickness = 1.dp
        )
    }
}
