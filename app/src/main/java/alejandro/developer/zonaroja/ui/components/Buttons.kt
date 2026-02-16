package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.R
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes textButton: Int,
    elevation: ButtonElevation = ButtonDefaults.buttonElevation()
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        elevation = elevation,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F).copy(alpha = 0.85f),
            disabledContainerColor = Color(0xFF808080).copy(alpha = 0.4f),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        )
    ) {
        Text(
            text = stringResource(textButton),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        )

    }
}

@Composable
fun LoginWithGoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = Color.Black.copy(alpha = 0.55f)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.login_with_google),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
fun RegisterButton(
    enabled: Boolean,
    onClick: () -> Unit,
    @StringRes textButton: Int,
    modifier: Modifier = Modifier
) {
    val gradient = if (enabled) {
        listOf(
            Color(0xFFE53935),
            Color(0xFFC62828)
        )
    } else {
        listOf(
            Color(0xFFBDBDBD),
            Color(0xFF9E9E9E)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)

            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )

            .background(
                brush = Brush.verticalGradient(gradient),
                shape = RoundedCornerShape(14.dp)
            )

            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(textButton),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.sp
        )
    }
}

