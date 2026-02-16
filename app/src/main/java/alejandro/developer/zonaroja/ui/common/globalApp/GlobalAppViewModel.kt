package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@Composable
inline fun <reified VM : ViewModel> activityHiltViewModel(): VM {
    val activity = LocalActivity.current as ComponentActivity
    return hiltViewModel(activity)
}