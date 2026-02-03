package alejandro.developer.zonaroja.ui.screens.register

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.components.ErrorEmailAndPasswordText
import alejandro.developer.zonaroja.ui.components.LoginButton
import alejandro.developer.zonaroja.ui.components.PrimaryRedButton
import alejandro.developer.zonaroja.ui.components.RedCircularProgress
import alejandro.developer.zonaroja.ui.components.RedOutlinedTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val currentContext by rememberUpdatedState(LocalContext.current)

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is RegisterUiEvent.BackToLogin -> onBackToLogin()
                is RegisterUiEvent.NavigateToMain -> onNavigateToMain()
                is RegisterUiEvent.ShowErrorRegister -> {
                    snackbarController.showErrorWithActionButton(
                        message = currentContext.getString(event.messageRes),
                        actionLabel = currentContext.getString(R.string.close_snackbar_button)
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)) // 👈 fondo descansado
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .background(Color(0xFFF7F7F7)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(56.dp))

            // TÍTULO
            Text(
                text = stringResource(R.string.create_new_account),
                color = Color(0xFFD32F2F),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(32.dp))

            // CARD CONTENEDORA
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(
                        color = Color.White,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {

                RedOutlinedTextField(
                    value = uiState.email,
                    textPlaceHolder = stringResource(R.string.email_register),
                    leadingIcon = Icons.Default.Email,
                    onValueChange = viewModel::onEmailChange,
                )

                if (uiState.showEmailErrorFormat) {
                    Spacer(Modifier.height(4.dp))
                    ErrorEmailAndPasswordText(R.string.email_format_invalid)
                }

                Spacer(Modifier.height(16.dp))

                RedOutlinedTextField(
                    value = uiState.password,
                    textPlaceHolder = stringResource(R.string.password_register),
                    leadingIcon = Icons.Default.Lock,
                    onValueChange = viewModel::onPasswordChange,
                    isPassword = true
                )

                if (uiState.showPasswordsDoNotMatchMessageText) {
                    Spacer(Modifier.height(4.dp))
                    ErrorEmailAndPasswordText(R.string.passwords_not_match)
                }

                Spacer(Modifier.height(16.dp))

                RedOutlinedTextField(
                    value = uiState.confirmPassword,
                    textPlaceHolder = stringResource(R.string.confirm_password_register),
                    leadingIcon = Icons.Default.Lock,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    isPassword = true
                )

                Spacer(Modifier.height(32.dp))

                // BOTÓN PROTAGONISTA
                PrimaryRedButton(
                    enabled = uiState.canRegister,
                    onClick = viewModel::onRegisterClick,
                    textButton = R.string.register_new_user_button
                )

            }

            Spacer(Modifier.height(24.dp))

            // FOOTER
            Row {
                Text(
                    text = stringResource(R.string.have_account),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.init_session_text_bottom),
                    color = Color(0xFFD32F2F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { viewModel.backToLogin() }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    RedCircularProgress(uiState.isLoading)
}
