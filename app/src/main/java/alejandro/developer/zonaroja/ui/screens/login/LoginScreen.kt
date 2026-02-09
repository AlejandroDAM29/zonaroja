package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.components.EmailTextField
import alejandro.developer.zonaroja.ui.components.LitleWhiteText
import alejandro.developer.zonaroja.ui.components.LoginButton
import alejandro.developer.zonaroja.ui.components.LoginWithGoogleButton
import alejandro.developer.zonaroja.ui.components.OrDivider
import alejandro.developer.zonaroja.ui.components.ZonaRojaTitle
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    successMessage: Boolean,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiEvents = LocalAppUiController.current
    val currentContext by rememberUpdatedState(LocalContext.current)

    LaunchedEffect(successMessage) {
        if (successMessage)
            appUiEvents.showSnackbarSuccess(currentContext.getString(R.string.logout_snackbar_success))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is LoginUiEvent.NavigateToMain -> navigateToMain()
                is LoginUiEvent.ShowErrorLogin -> {
                    appUiEvents.showSnackbarErrorWithActionButton(
                        event.message,
                        event.actionLabelText,
                        event.onAction
                    )
                }

                is LoginUiEvent.ShowErrorRegister -> {
                    appUiEvents.showSnackbarError(currentContext.getString(event.messageRes))
                }

                is LoginUiEvent.ShowSuccessRegister -> {
                    appUiEvents.showSnackbarSuccess(event.message)
                }
            }
        }
    }

    BaseScreen(
        isLoading = uiState.isLoading
    ) {
        ContentLoginScreen(
            uiState = uiState,
            viewModel = viewModel,
            currentContext = currentContext,
            navigateToRegister = navigateToRegister
        )
    }
}

@Composable
fun ContentLoginScreen(
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    currentContext: Context,
    navigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.drawable.zona_roja_bg),
                contentScale = ContentScale.Crop
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.zona_roja_warning_icon),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ZonaRojaTitle(
                text1 = stringResource(R.string.title_text_1),
                text2 = stringResource(R.string.title_text_2)
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                enabled = uiState.canSubmit,
                onClick = viewModel::doLogin,
                textButton = R.string.init_session_button
            )

            Spacer(Modifier.height(16.dp))

            //Google functions
            if (uiState.isGoogleLoginEnabled) {

                LoginGoogle(
                    viewModel = viewModel,
                    currentContext = currentContext
                )

                Spacer(Modifier.height(12.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LitleWhiteText(
                        text = stringResource(R.string.forgot_password),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    LitleWhiteText(
                        text = stringResource(R.string.create_account_login),
                        onClick = navigateToRegister
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }


    }

}

@Composable
fun LoginGoogle(
    viewModel: LoginViewModel,
    currentContext: Context
) {
    val credentialManager = remember {
        CredentialManager.create(currentContext)
    }

    val googleIdOption = remember {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(
                currentContext.getString(R.string.default_web_client_id)
            )
            .build()
    }

    val getCredentialRequest = remember {
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    val coroutineScope = rememberCoroutineScope()
    Spacer(Modifier.height(16.dp))
    OrDivider()
    Spacer(Modifier.height(16.dp))

    LoginWithGoogleButton(
        onClick = {
            coroutineScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        context = currentContext,
                        request = getCredentialRequest
                    )

                    val credential = result.credential

                    if (credential is GoogleIdTokenCredential) {
                        viewModel.onGoogleTokenReceived(credential.idToken)
                    } else {
                        viewModel.onGoogleTokenReceived(null)
                    }

                } catch (_: GetCredentialException) {
                    viewModel.onGoogleTokenReceived(null)
                }
            }
        }
    )
}