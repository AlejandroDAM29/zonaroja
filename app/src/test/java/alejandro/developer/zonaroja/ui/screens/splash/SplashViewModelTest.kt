package alejandro.developer.zonaroja.ui.screens.splash

import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.usecase.CheckUserSessionUseCase
import alejandro.developer.zonaroja.MainDispatcherRule
import alejandro.developer.zonaroja.sampleFeatureFlagsModel
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val checkUserSessionUseCase: CheckUserSessionUseCase = mock()
    private val featureFlagsProvider: FeatureFlagsProvider = mock()

    @Test
    fun init_prefetchesFeatureFlags() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel())

        SplashViewModel(checkUserSessionUseCase, featureFlagsProvider)
        advanceUntilIdle()

        verify(featureFlagsProvider).get()
    }

    @Test
    fun onSplashShown_emitsNavigateToMain_whenUserHasSession() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel())
        whenever(checkUserSessionUseCase.invoke()).thenReturn(true)
        val viewModel = SplashViewModel(checkUserSessionUseCase, featureFlagsProvider)
        advanceUntilIdle()

        viewModel.uiEvent.test {
            viewModel.onSplashShown()
            advanceUntilIdle()

            assertEquals(SplashUiEvent.NavigateToMain, awaitItem())
        }
    }

    @Test
    fun onSplashShown_emitsNavigateToLogin_whenUserHasNoSession() = runTest(mainDispatcherRule.dispatcher) {
        whenever(featureFlagsProvider.get()).thenReturn(sampleFeatureFlagsModel())
        whenever(checkUserSessionUseCase.invoke()).thenReturn(false)
        val viewModel = SplashViewModel(checkUserSessionUseCase, featureFlagsProvider)
        advanceUntilIdle()

        viewModel.uiEvent.test {
            viewModel.onSplashShown()
            advanceUntilIdle()

            assertEquals(SplashUiEvent.NavigateToLogin, awaitItem())
        }
    }
}
