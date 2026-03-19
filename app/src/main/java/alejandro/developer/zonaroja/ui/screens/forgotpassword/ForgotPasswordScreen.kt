package alejandro.developer.zonaroja.ui.screens.forgotpassword


import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.components.ErrorEmailAndPasswordText
import alejandro.developer.zonaroja.ui.components.RedOutlinedTextField
import alejandro.developer.zonaroja.ui.components.RegisterButton
import alejandro.developer.zonaroja.ui.theme.GraseDescriptionsText
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
   viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiEvents = LocalAppUiController.current
    val currentContext by rememberUpdatedState(LocalContext.current)

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is ForgotPasswordUiEvent.ShowSuccessResendPassword ->
                    appUiEvents.showSnackbarSuccess(currentContext.getString(R.string.resend_password_success_message))

                is ForgotPasswordUiEvent.ShowErrorResendPassword -> {
                    appUiEvents.showSnackbarSuccess(currentContext.getString(R.string.resend_email_error_message))
                }
            }
        }
    }

    ContentForgotPasswordScreen(
        onBackToLogin = onBackToLogin,
        uiState = uiState,
        viewModel = viewModel
        )
}


@Composable
fun ContentForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    uiState: ForgotPasswordUiState,
    viewModel: ForgotPasswordViewModel
) {
    BaseScreen(uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(56.dp))

                Text(
                    text = stringResource(R.string.forgot_password_title),
                    color = Color(0xFFD32F2F),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(122.dp)
                        .height(1.dp)
                        .background(
                            color = Color(0xFFD32F2F),
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.resend_email_description),
                    color = GraseDescriptionsText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                RedOutlinedTextField(
                    value = uiState.email,
                    textPlaceHolder = stringResource(R.string.email_forgot_password),
                    leadingIcon = Icons.Default.Email,
                    onValueChange = viewModel::onEmailChange
                )

                if (uiState.showEmailErrorFormat) {
                    Spacer(Modifier.height(4.dp))
                    ErrorEmailAndPasswordText(R.string.email_format_invalid)
                }

                Spacer(Modifier.height(24.dp))

                RegisterButton(
                    enabled = uiState.canSubmit,
                    onClick = { viewModel.sendPasswordResetEmail(uiState.email) },
                    textButton = R.string.register_new_user_button
                )

                Spacer(Modifier.weight(1f))


                Text(
                    text = stringResource(R.string.remember_password),
                    color = GraseDescriptionsText,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.comeback_login_from_resend_password),
                    color = RedZoneColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onBackToLogin() }
                )


                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
