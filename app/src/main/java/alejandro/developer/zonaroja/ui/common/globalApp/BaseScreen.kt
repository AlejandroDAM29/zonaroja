package alejandro.developer.zonaroja.ui.common.globalApp

import alejandro.developer.zonaroja.ui.components.RedCircularProgress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable

@Composable
fun BaseScreen(
    isLoading: Boolean,
    loadingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box {
        content()

        if (isLoading) {
            loadingContent?.invoke() ?: RedCircularProgress()
        }
    }
}