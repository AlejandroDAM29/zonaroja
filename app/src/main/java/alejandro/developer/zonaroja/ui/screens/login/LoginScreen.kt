package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.BaseScreen
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.components.EmailTextField
import alejandro.developer.zonaroja.ui.components.LoginButton
import alejandro.developer.zonaroja.ui.components.RedCircularProgress
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val currentContext by rememberUpdatedState(LocalContext.current)

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(currentContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(currentContext, googleSignInOptions)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.onGoogleTokenReceived(account.idToken)
            } catch (e: ApiException) {
                viewModel.onGoogleError()
            }
        } else {
            viewModel.onGoogleError()
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
            googleLauncher = googleLauncher,
            googleSignInClient = googleSignInClient
        )
    }
}

@Composable
fun ContentLoginScreen(
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    googleLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    googleSignInClient: GoogleSignInClient
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
            LoginButton(
                onClick = {
                    googleLauncher.launch(
                        googleSignInClient.signInIntent
                    )
                },
                enabled = true,
                modifier = Modifier.fillMaxWidth()
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