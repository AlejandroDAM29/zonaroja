package alejandro.developer.zonaroja.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BaseScreen(
    isLoading: Boolean,
    loadingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()

            if (isLoading && loadingContent != null) {
                loadingContent()
            }
        }

}
