package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LitleWhiteText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .clickable { onClick() },
        style = TextStyle(
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textDecoration = TextDecoration.Underline
        )
    )
}

@Composable
fun ErrorEmailAndPasswordText(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(text),
        color = RedZoneColor,
        fontSize = 12.sp,
        textAlign = TextAlign.Start,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
}

