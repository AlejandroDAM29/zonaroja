package alejandro.developer.zonaroja.ui.screens.forgotpassword

import alejandro.developer.core.network.NoInternetException
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.SendPasswordResetEmailUseCase
import alejandro.developer.zonaroja.MainDispatcherRule
import alejandro.developer.zonaroja.R
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase = mock()

    @Test
    fun onEmailChange_updatesUiState() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = ForgotPasswordViewModel(sendPasswordResetEmailUseCase, AppDataMode.BACKEND)

        viewModel.onEmailChange("user@example.com")

        assertEquals("user@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun sendPasswordResetEmail_emitsSuccessMessageAndClearsState() = runTest(mainDispatcherRule.dispatcher) {
        whenever(sendPasswordResetEmailUseCase.invoke("user@example.com")).thenReturn(Result.success(Unit))
        val viewModel = ForgotPasswordViewModel(sendPasswordResetEmailUseCase, AppDataMode.BACKEND)
        viewModel.onEmailChange("user@example.com")

        viewModel.uiEvents.test {
            viewModel.sendPasswordResetEmail("user@example.com")
            advanceUntilIdle()

            assertEquals(
                ForgotPasswordUiEvent.ShowSuccessResendPassword(R.string.resend_password_success_message),
                awaitItem()
            )
            assertEquals("", viewModel.uiState.value.email)
            assertFalse(viewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun sendPasswordResetEmail_emitsNetworkErrorWhenRequestFailsByConnectivity() = runTest(mainDispatcherRule.dispatcher) {
        whenever(sendPasswordResetEmailUseCase.invoke("user@example.com"))
            .thenReturn(Result.failure(NoInternetException()))
        val viewModel = ForgotPasswordViewModel(sendPasswordResetEmailUseCase, AppDataMode.BACKEND)

        viewModel.uiEvents.test {
            viewModel.sendPasswordResetEmail("user@example.com")
            advanceUntilIdle()

            assertEquals(
                ForgotPasswordUiEvent.ShowErrorResendPassword(R.string.error_auth_network),
                awaitItem()
            )
            assertFalse(viewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun sendPasswordResetEmail_usesModsSuccessMessageWhenRunningInModsMode() = runTest(mainDispatcherRule.dispatcher) {
        whenever(sendPasswordResetEmailUseCase.invoke("mods@example.com")).thenReturn(Result.success(Unit))
        val viewModel = ForgotPasswordViewModel(sendPasswordResetEmailUseCase, AppDataMode.MODS)

        viewModel.uiEvents.test {
            viewModel.sendPasswordResetEmail("mods@example.com")
            advanceUntilIdle()

            assertEquals(
                ForgotPasswordUiEvent.ShowSuccessResendPassword(R.string.mods_password_reset_disabled_message),
                awaitItem()
            )
        }
    }
}
