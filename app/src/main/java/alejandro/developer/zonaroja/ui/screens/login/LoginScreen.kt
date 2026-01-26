package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.zonaroja.ui.screens.main.MainViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        Text("Login SCREEN", fontSize = 30.sp)
        Spacer(Modifier.weight(1f))
        Button(onClick ={
            viewModel.getTokenAccess()
            navigateToMain()
        } ) {
            Text("Navegar")
        }
        Spacer(Modifier.weight(1f))
    }
}