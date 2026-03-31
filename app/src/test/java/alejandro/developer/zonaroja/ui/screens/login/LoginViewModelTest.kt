package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.usecase.LoginWithEmailUseCase
import alejandro.developer.domain.usecase.LoginWithGoogleUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.MainDispatcherRule
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.sampleFeatureFlagsModel
import app.cash.turbine.test
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginWithEmailUseCase: LoginWithEmailUseCase = mock()
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase = mock()
    private val featureFlagsProvider: FeatureFlagsProvider = mock()
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase = mock()
    private val networkMonitor: NetworkMonitor = mock()

    @Test
    fun init_enablesGoogleLoginWhenBackendAndFlagIsActive() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = true))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())

        val viewModel = buildViewModel(appDataMode = AppDataMode.BACKEND)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGoogleLoginEnabled)
        assertFalse(viewModel.uiState.value.isValidationBypassed)
    }

    @Test
    fun init_bypassesValidationInModsModeAndDisablesGoogleLogin() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = true))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())

        val viewModel = buildViewModel(appDataMode = AppDataMode.MODS)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isGoogleLoginEnabled)
        assertTrue(viewModel.uiState.value.isValidationBypassed)
    }

    @Test
    fun doLogin_emitsNavigateToMainAndClearsCredentialsOnSuccess() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = false))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())
        whenever(loginWithEmailUseCase.invoke("user@example.com", "secret1"))
            .thenReturn(Result.success(Unit))
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("secret1")

        viewModel.uiEvents.test {
            viewModel.doLogin()
            advanceUntilIdle()

            assertEquals(LoginUiEvent.NavigateToMain, awaitItem())
            assertEquals("", viewModel.uiState.value.email)
            assertEquals("", viewModel.uiState.value.password)
            assertFalse(viewModel.uiState.value.isLoading)
            verify(syncNotificationSubscriptionsUseCase).invoke()
        }
    }

    @Test
    fun doLogin_emitsMappedErrorOnFailure() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = false))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())
        whenever(loginWithEmailUseCase.invoke("user@example.com", "secret1")).thenReturn(
            Result.failure(
                FirebaseAuthInvalidCredentialsException(
                    "ERROR_INVALID_CREDENTIAL",
                    "Bad credentials"
                )
            )
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("secret1")

        viewModel.uiEvents.test {
            viewModel.doLogin()
            advanceUntilIdle()

            assertEquals(
                LoginUiEvent.ShowErrorLogin(R.string.error_auth_invalid_credentials),
                awaitItem()
            )
            assertFalse(viewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun onGoogleLoginClicked_returnsFalseAndEmitsNetworkErrorWhenOffline() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = false))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())
        whenever(networkMonitor.isCurrentlyOnline()).thenReturn(false)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiEvents.test {
            val result = viewModel.onGoogleLoginClicked()
            advanceUntilIdle()

            assertFalse(result)
            assertEquals(
                LoginUiEvent.ShowErrorGoogleRegister(R.string.error_auth_network),
                awaitItem()
            )
        }
    }

    @Test
    fun onGoogleTokenReceived_emitsResponseErrorWhenTokenIsBlankAndOnline() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = false))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())
        whenever(networkMonitor.isCurrentlyOnline()).thenReturn(true)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiEvents.test {
            viewModel.onGoogleTokenReceived(null)
            advanceUntilIdle()

            assertEquals(
                LoginUiEvent.ShowErrorGoogleRegister(R.string.error_auth_google_response),
                awaitItem()
            )
        }
    }

    @Test
    fun onGoogleTokenReceived_emitsNavigateToMainOnSuccess() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = false))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())
        whenever(loginWithGoogleUseCase.invoke("token")).thenReturn(Result.success(Unit))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiEvents.test {
            viewModel.onGoogleTokenReceived("token")
            advanceUntilIdle()

            assertEquals(LoginUiEvent.NavigateToMain, awaitItem())
            assertFalse(viewModel.uiState.value.isLoading)
            verify(syncNotificationSubscriptionsUseCase).invoke()
        }
    }

    @Test
    fun onGoogleLoginFailure_emitsMappedFirebaseError() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel(enabled = false))
        whenever(networkMonitor.isOnline).thenReturn(emptyFlow())
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiEvents.test {
            viewModel.onGoogleLoginFailure(
                FirebaseAuthUserCollisionException(
                    "ERROR_EMAIL_ALREADY_IN_USE",
                    "Already exists"
                )
            )
            advanceUntilIdle()

            assertEquals(
                LoginUiEvent.ShowErrorGoogleRegister(R.string.error_auth_google_account_exists),
                awaitItem()
            )
        }
    }

    private fun buildViewModel(appDataMode: AppDataMode = AppDataMode.BACKEND): LoginViewModel {
        return LoginViewModel(
            loginWithEmailUseCase = loginWithEmailUseCase,
            loginWithGoogle = loginWithGoogleUseCase,
            featureFlagsProvider = featureFlagsProvider,
            syncNotificationSubscriptionsUseCase = syncNotificationSubscriptionsUseCase,
            networkMonitor = networkMonitor,
            appDataMode = appDataMode
        )
    }
}
