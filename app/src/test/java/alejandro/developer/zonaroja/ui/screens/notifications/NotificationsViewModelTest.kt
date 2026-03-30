package alejandro.developer.zonaroja.ui.screens.notifications

import alejandro.developer.domain.usecase.DeleteNotificationUseCase
import alejandro.developer.domain.usecase.ObserveNotificationsUseCase
import alejandro.developer.zonaroja.MainDispatcherRule
import alejandro.developer.zonaroja.sampleNotificationModel
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeNotificationsUseCase: ObserveNotificationsUseCase = mock()
    private val deleteNotificationUseCase: DeleteNotificationUseCase = mock()

    @Test
    fun uiState_mapsNotificationsIntoLoadedState() = runTest(mainDispatcherRule.dispatcher) {
        whenever(observeNotificationsUseCase.invoke()).thenReturn(flowOf(listOf(sampleNotificationModel())))
        val viewModel = NotificationsViewModel(observeNotificationsUseCase, deleteNotificationUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            val loaded = awaitItem()

            assertTrue(initial.isLoading)
            assertFalse(loaded.isLoading)
            assertEquals(listOf(sampleNotificationModel()), loaded.notifications)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onDeleteClicked_invokesDeleteUseCase() = runTest(mainDispatcherRule.dispatcher) {
        whenever(observeNotificationsUseCase.invoke()).thenReturn(flowOf(emptyList()))
        val viewModel = NotificationsViewModel(observeNotificationsUseCase, deleteNotificationUseCase)

        viewModel.onDeleteClicked(15L)
        advanceUntilIdle()

        verify(deleteNotificationUseCase).invoke(15L)
    }
}
