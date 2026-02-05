package alejandro.developer.zonaroja.ui.common

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.components.AppTopBar
import alejandro.developer.zonaroja.ui.components.RedCircularProgress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
@Composable
fun BaseScreen(
    isLoading: Boolean,
    loadingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.fillMaxSize()) {
            content()

            if (isLoading) {
                loadingContent?.invoke() ?: RedCircularProgress()
            }
        }
    }
}
