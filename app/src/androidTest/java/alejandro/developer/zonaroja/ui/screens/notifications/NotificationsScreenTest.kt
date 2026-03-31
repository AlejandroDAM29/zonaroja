package alejandro.developer.zonaroja.ui.screens.notifications

import alejandro.developer.domain.usecase.DeleteNotificationUseCase
import alejandro.developer.domain.usecase.ObserveNotificationsUseCase
import alejandro.developer.zonaroja.FakeNotificationRepository
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.setZonaRojaContent
import alejandro.developer.zonaroja.testNotification
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun emptyState_isShownWhenThereAreNoNotifications() {
        val repository = FakeNotificationRepository()
        val viewModel = NotificationsViewModel(
            observeNotificationsUseCase = ObserveNotificationsUseCase(repository),
            deleteNotificationUseCase = DeleteNotificationUseCase(repository)
        )

        composeRule.setZonaRojaContent {
            NotificationsScreen(
                onBack = {},
                onOpenNotification = {},
                viewModel = viewModel
            )
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.notifications_empty_title)
            ).fetchSemanticsNodes().isNotEmpty()
        }

        assertEquals(
            1,
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.notifications_empty_title)
            ).fetchSemanticsNodes().size
        )
    }

    @Test
    fun openAndDeleteActions_delegateToCallbackAndRepository() {
        val repository = FakeNotificationRepository(
            initialNotifications = listOf(testNotification())
        )
        val viewModel = NotificationsViewModel(
            observeNotificationsUseCase = ObserveNotificationsUseCase(repository),
            deleteNotificationUseCase = DeleteNotificationUseCase(repository)
        )
        var openedNotificationId: Long? = null

        composeRule.setZonaRojaContent {
            NotificationsScreen(
                onBack = {},
                onOpenNotification = { openedNotificationId = it },
                viewModel = viewModel
            )
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Alerta de prueba").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Alerta de prueba").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) { openedNotificationId != null }
        assertEquals(1L, openedNotificationId)

        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.notifications_delete_description)
        ).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Alerta de prueba").fetchSemanticsNodes().isEmpty()
        }

        assertEquals(
            0,
            composeRule.onAllNodesWithText("Alerta de prueba").fetchSemanticsNodes().size
        )
    }
}
