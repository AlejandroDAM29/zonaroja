package alejandro.developer.zonaroja.ui.screens.splash

import alejandro.developer.zonaroja.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToLogin: () -> Unit,
    navigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(1_500)
        viewModel.onSplashShown()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                SplashUiEvent.NavigateToLogin -> navigateToLogin()
                SplashUiEvent.NavigateToMain -> navigateToMain()
            }
        }
    }
    ContentSplashScreen()
}

@Composable
fun ContentSplashScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Alejandro Studios",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFD32F2F),
            fontFamily = FontFamily(Font(R.font.ochenta_db)),// rojo potente
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            lineHeight = 58.sp,
            textAlign = TextAlign.Center
        )
    }
}
