package alejandro.developer.zonaroja.ui.screens.setting

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.zonaroja.setZonaRojaContent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun themeSwitch_invokesCallbackWithUpdatedValue() {
        val themeChanges = mutableListOf<Boolean>()

        composeRule.setZonaRojaContent {
            SettingsContent(
                uiState = SettingUiState(
                    email = "user@example.com",
                    darkThemeEnabled = false
                ),
                hasNotificationPermission = true,
                showDeleteDialog = false,
                showCurrencySheet = false,
                onClose = {},
                onChangeTheme = { themeChanges += it },
                onNotificationsChanged = {},
                onOpenCurrencySelector = {},
                onDismissCurrencySelector = {},
                onCurrencySelected = {},
                onLogoutClicked = {},
                onChangePasswordClicked = {},
                onShowDeleteDialog = {},
                onDismissDeleteDialog = {},
                onDeleteAccountConfirmed = {},
                showDeleteReauthDialog = false,
                deletePassword = "",
                onDeletePasswordChanged = {},
                onDismissDeleteReauthDialog = {},
                onDeleteAccountReauthenticated = {}
            )
        }

        composeRule.onNodeWithTag("settings_theme_switch").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { themeChanges.isNotEmpty() }
        assertEquals(listOf(true), themeChanges)
    }

    @Test
    fun currencyRow_opensSheetAndReturnsSelectedCurrency() {
        var selectedCurrency: AppCurrency? = null

        composeRule.setZonaRojaContent {
            var showCurrencySheet by remember { mutableStateOf(false) }

            SettingsContent(
                uiState = SettingUiState(
                    email = "user@example.com",
                    selectedCurrency = AppCurrency.EUR
                ),
                hasNotificationPermission = true,
                showDeleteDialog = false,
                showCurrencySheet = showCurrencySheet,
                onClose = {},
                onChangeTheme = {},
                onNotificationsChanged = {},
                onOpenCurrencySelector = { showCurrencySheet = true },
                onDismissCurrencySelector = { showCurrencySheet = false },
                onCurrencySelected = {
                    selectedCurrency = it
                    showCurrencySheet = false
                },
                onLogoutClicked = {},
                onChangePasswordClicked = {},
                onShowDeleteDialog = {},
                onDismissDeleteDialog = {},
                onDeleteAccountConfirmed = {},
                showDeleteReauthDialog = false,
                deletePassword = "",
                onDeletePasswordChanged = {},
                onDismissDeleteReauthDialog = {},
                onDeleteAccountReauthenticated = {}
            )
        }

        composeRule.onNodeWithTag("settings_currency_row").performClick()
        composeRule.onNodeWithTag("settings_currency_option_USD").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { selectedCurrency == AppCurrency.USD }
        assertTrue(selectedCurrency == AppCurrency.USD)
    }
}
