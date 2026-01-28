package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.BaseScreen
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.components.EmailTextField
import alejandro.developer.zonaroja.ui.components.LoginButton
import alejandro.developer.zonaroja.ui.components.RedCircularProgress
import alejandro.developer.zonaroja.ui.screens.main.MainUiEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val currentContext by rememberUpdatedState(LocalContext.current)


    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is LoginUiEvent.NavigateToMain -> navigateToMain()
                is LoginUiEvent.ShowErrorLogin -> {
                    snackbarController.showErrorWithActionButton(
                        event.message,
                        event.actionLabelText,
                        event.onAction)
                }
                is LoginUiEvent.ShowErrorRegister -> {
                    snackbarController.showError(currentContext.getString(event.messageRes))
                }
                is LoginUiEvent.ShowSuccessRegister -> {
                    snackbarController.showSuccess(event.message)
                }
            }
        }
    }

    BaseScreen(
        isLoading = uiState.isLoading
    ) {
        ContentLoginScreen(
            uiState = uiState,
            navigateToMain = navigateToMain,
            viewModel = viewModel
        )
    }
}

@Composable
fun ContentLoginScreen(
    uiState: LoginUiState,
    navigateToMain: () -> Unit,
    viewModel: LoginViewModel
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.drawable.zona_roja_bg),
                contentScale = ContentScale.Crop
            )
    ) {

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            EmailTextField(
                value = uiState.email,
                textPlaceHolder = stringResource(R.string.mail_placeholder),
                leadingIcon = Icons.Default.Email,
                onValueChange = viewModel::onEmailChange,
            )
            Spacer(Modifier.height(8.dp))
            EmailTextField(
                value = uiState.password,
                textPlaceHolder = stringResource(R.string.password_placeholder),
                leadingIcon = Icons.Default.Lock,
                onValueChange = viewModel::onPasswordChange,
                isPassword = true
            )
            Spacer(Modifier.height(16.dp))
            LoginButton(
                uiState.canSubmit,
                onClick = viewModel::doLogin
            )
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            RedCircularProgress()
        }
    }

}