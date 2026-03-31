package alejandro.developer.zonaroja.ui.screens.notificationdetail

import alejandro.developer.domain.usecase.MarkNotificationAsReadUseCase
import alejandro.developer.domain.usecase.ObserveNotificationByIdUseCase
import alejandro.developer.zonaroja.MainDispatcherRule
import alejandro.developer.zonaroja.sampleNotificationModel
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeNotificationByIdUseCase: ObserveNotificationByIdUseCase = mock()
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase = mock()

    @Test
    fun onNotificationOpened_marksAsReadAndLoadsNotification() = runTest(mainDispatcherRule.dispatcher) {
        whenever(observeNotificationByIdUseCase.invoke(15L)).thenReturn(flowOf(sampleNotificationModel()))
        val viewModel = NotificationDetailViewModel(
            observeNotificationByIdUseCase = observeNotificationByIdUseCase,
            markNotificationAsReadUseCase = markNotificationAsReadUseCase
        )

        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)

            viewModel.onNotificationOpened(15L)
            advanceUntilIdle()

            val loaded = awaitItem()
            assertEquals(sampleNotificationModel(), loaded.notification)
            verify(markNotificationAsReadUseCase).invoke(15L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onNotificationOpened_ignoresRepeatedIds() = runTest(mainDispatcherRule.dispatcher) {
        whenever(observeNotificationByIdUseCase.invoke(15L)).thenReturn(flowOf(sampleNotificationModel()))
        val viewModel = NotificationDetailViewModel(
            observeNotificationByIdUseCase = observeNotificationByIdUseCase,
            markNotificationAsReadUseCase = markNotificationAsReadUseCase
        )

        viewModel.onNotificationOpened(15L)
        viewModel.onNotificationOpened(15L)
        advanceUntilIdle()

        verify(markNotificationAsReadUseCase, times(1)).invoke(15L)
    }
}
