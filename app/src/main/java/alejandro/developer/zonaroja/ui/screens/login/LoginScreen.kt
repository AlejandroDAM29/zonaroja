package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.BaseScreen
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.components.EmailTextField
import alejandro.developer.zonaroja.ui.components.LoginButton
import alejandro.developer.zonaroja.ui.components.LoginWithGoogleButton
import alejandro.developer.zonaroja.ui.components.OrDivider
import alejandro.developer.zonaroja.ui.components.RedCircularProgress
import alejandro.developer.zonaroja.ui.components.ZonaRojaTitle
import android.content.Context
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
    successMessage: String? = null,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val currentContext by rememberUpdatedState(LocalContext.current)
    val credentialManager = remember {
        CredentialManager.create(currentContext)
    }

    //Google login system - - - - - - - - - -
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
    // - - - - - - - - - - - - - - - - - - - -

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarController.showSuccess(it)
        }
    }

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
            viewModel = viewModel,
            credentialManager = credentialManager,
            getCredentialRequest = getCredentialRequest,
            currentContext = currentContext
        )
    }
}

@Composable
fun ContentLoginScreen(
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    credentialManager: CredentialManager,
    getCredentialRequest: GetCredentialRequest,
    currentContext: Context
){
    val coroutineScope = rememberCoroutineScope()
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ZonaRojaTitle(
                text1 = stringResource(R.string.title_text_1),
                text2 = stringResource(R.string.title_text_2)
            )
            Spacer(Modifier.height(20.dp))
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

                        } catch (e: GetCredentialException) {
                            viewModel.onGoogleTokenReceived(null)
                        }
                    }
                }
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