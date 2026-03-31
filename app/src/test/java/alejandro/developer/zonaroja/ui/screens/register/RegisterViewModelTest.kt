package alejandro.developer.zonaroja.ui.screens.register

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.RegisterWithEmailUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.MainDispatcherRule
import alejandro.developer.zonaroja.R
import app.cash.turbine.test
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val registerWithEmailUseCase: RegisterWithEmailUseCase = mock()
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase = mock()

    @Test
    fun init_enablesValidationBypassInModsMode() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = RegisterViewModel(
            registerWithEmail = registerWithEmailUseCase,
            syncNotificationSubscriptionsUseCase = syncNotificationSubscriptionsUseCase,
            appDataMode = AppDataMode.MODS
        )

        assertTrue(viewModel.uiState.value.isValidationBypassed)
    }

    @Test
    fun onRegisterClick_emitsNavigateToMainOnSuccess() = runTest(mainDispatcherRule.dispatcher) {
        whenever(registerWithEmailUseCase.invoke("user@example.com", "secret1"))
            .thenReturn(Result.success(Unit))
        val viewModel = RegisterViewModel(
            registerWithEmail = registerWithEmailUseCase,
            syncNotificationSubscriptionsUseCase = syncNotificationSubscriptionsUseCase,
            appDataMode = AppDataMode.BACKEND
        )
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("secret1")

        viewModel.uiEvents.test {
            viewModel.onRegisterClick()
            advanceUntilIdle()

            assertEquals(RegisterUiEvent.NavigateToMain, awaitItem())
            assertFalse(viewModel.uiState.value.isLoading)
            verify(syncNotificationSubscriptionsUseCase).invoke()
        }
    }

    @Test
    fun onRegisterClick_emitsUserExistsMessageOnCollision() = runTest(mainDispatcherRule.dispatcher) {
        whenever(registerWithEmailUseCase.invoke("user@example.com", "secret1")).thenReturn(
            Result.failure(
                FirebaseAuthUserCollisionException(
                    "ERROR_EMAIL_ALREADY_IN_USE",
                    "Already exists"
                )
            )
        )
        val viewModel = RegisterViewModel(
            registerWithEmail = registerWithEmailUseCase,
            syncNotificationSubscriptionsUseCase = syncNotificationSubscriptionsUseCase,
            appDataMode = AppDataMode.BACKEND
        )
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("secret1")

        viewModel.uiEvents.test {
            viewModel.onRegisterClick()
            advanceUntilIdle()

            assertEquals(
                RegisterUiEvent.ShowErrorRegister(R.string.error_auth_user_exists),
                awaitItem()
            )
            assertFalse(viewModel.uiState.value.isLoading)
        }
    }
}
