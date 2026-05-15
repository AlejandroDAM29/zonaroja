package alejandro.developer.zonaroja.ui.screens.setting

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.AppViewModel
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.activityHiltViewModel
import alejandro.developer.zonaroja.ui.components.RedOutlinedTextField
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingScreen(
    onClose: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appViewModel: AppViewModel = activityHiltViewModel()
    val appUiController = LocalAppUiController.current
    val context by rememberUpdatedState(LocalContext.current)
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasNotificationPermission by remember {
        mutableStateOf(context.hasNotificationPermission())
    }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteReauthDialog by rememberSaveable { mutableStateOf(false) }
    var deletePassword by rememberSaveable { mutableStateOf("") }
    var showCurrencySheet by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
        if (granted) {
            viewModel.onNotificationsChanged(true)
        } else {
            viewModel.onNotificationPermissionRevoked()
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationPermission = context.hasNotificationPermission()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(hasNotificationPermission, uiState.notificationsEnabled) {
        if (!hasNotificationPermission && uiState.notificationsEnabled) {
            viewModel.onNotificationPermissionRevoked()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is SettingUiEvent.NavigateToLogin -> onNavigateToLogin()

                is SettingUiEvent.NavigateToLoginLogoutSuccess -> {
                    onNavigateToLogin()
                    appViewModel.showSnackbarSuccess(
                        context.getString(event.messageRes)
                    )
                }

                is SettingUiEvent.RequestDeleteAccountReauthentication -> {
                    showDeleteDialog = false
                    showDeleteReauthDialog = true
                }

                is SettingUiEvent.ShowMessage -> {
                    when (event.type) {
                        SettingMessageType.SUCCESS -> {
                            appUiController.showSnackbarSuccess(
                                context.getString(event.messageRes)
                            )
                        }

                        SettingMessageType.ERROR -> {
                            appUiController.showSnackbarError(
                                context.getString(event.messageRes)
                            )
                        }

                        SettingMessageType.WARNING -> {
                            appUiController.showSnackbarWarning(
                                context.getString(event.messageRes)
                            )
                        }
                    }
                }
            }
        }
    }

    BaseScreen(isLoading = uiState.isLoading) {
        SettingsContent(
            uiState = uiState,
            hasNotificationPermission = hasNotificationPermission,
            showDeleteDialog = showDeleteDialog,
            showCurrencySheet = showCurrencySheet,
            onClose = onClose,
            onChangeTheme = viewModel::onDarkThemeChanged,
            onNotificationsChanged = { enabled ->
                if (enabled && !hasNotificationPermission) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.onNotificationsChanged(true)
                    }
                } else {
                    viewModel.onNotificationsChanged(enabled)
                }
            },
            onOpenCurrencySelector = { showCurrencySheet = true },
            onDismissCurrencySelector = { showCurrencySheet = false },
            onCurrencySelected = {
                viewModel.onCurrencySelected(it)
                showCurrencySheet = false
            },
            onLogoutClicked = viewModel::onLogoutClicked,
            onChangePasswordClicked = onNavigateToChangePassword,
            onShowDeleteDialog = { showDeleteDialog = true },
            onDismissDeleteDialog = { showDeleteDialog = false },
            onDeleteAccountConfirmed = {
                showDeleteDialog = false
                viewModel.onDeleteAccountConfirmed()
            },
            showDeleteReauthDialog = showDeleteReauthDialog,
            deletePassword = deletePassword,
            onDeletePasswordChanged = { deletePassword = it },
            onDismissDeleteReauthDialog = {
                showDeleteReauthDialog = false
                deletePassword = ""
            },
            onDeleteAccountReauthenticated = {
                showDeleteReauthDialog = false
                viewModel.onDeleteAccountConfirmed(deletePassword)
                deletePassword = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    uiState: SettingUiState,
    hasNotificationPermission: Boolean,
    showDeleteDialog: Boolean,
    showCurrencySheet: Boolean,
    onClose: () -> Unit,
    onChangeTheme: (Boolean) -> Unit,
    onNotificationsChanged: (Boolean) -> Unit,
    onOpenCurrencySelector: () -> Unit,
    onDismissCurrencySelector: () -> Unit,
    onCurrencySelected: (AppCurrency) -> Unit,
    onLogoutClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onDeleteAccountConfirmed: () -> Unit,
    showDeleteReauthDialog: Boolean,
    deletePassword: String,
    onDeletePasswordChanged: (String) -> Unit,
    onDismissDeleteReauthDialog: () -> Unit,
    onDeleteAccountReauthenticated: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    val secondarySurface = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    val onSurface = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = RedZoneColor,
        checkedBorderColor = RedZoneColor,
        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
        uncheckedBorderColor = MaterialTheme.colorScheme.outline
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.comparison_close_description),
                        tint = RedZoneColor
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.email.firstOrNull()?.uppercaseChar()?.toString()
                                    ?: "A",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = RedZoneColor
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = uiState.email.ifBlank {
                                stringResource(R.string.settings_email_fallback)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onLogoutClicked),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = RedZoneColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.settings_logout),
                                style = MaterialTheme.typography.titleMedium,
                                color = RedZoneColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Text(
                text = stringResource(R.string.settings_preferences),
                style = MaterialTheme.typography.titleMedium,
                color = subTextColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.settings_notifications),
                    subtitle = if (!hasNotificationPermission) {
                        stringResource(R.string.settings_notifications_permission_required)
                    } else {
                        null
                    },
                    checked = uiState.notificationsEnabled && hasNotificationPermission,
                    onCheckedChange = onNotificationsChanged,
                    switchColors = switchColors,
                    switchTestTag = "settings_notifications_switch"
                )

                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.settings_theme),
                    subtitle = if (uiState.darkThemeEnabled) {
                        stringResource(R.string.settings_theme_dark)
                    } else {
                        stringResource(R.string.settings_theme_light)
                    },
                    checked = uiState.darkThemeEnabled,
                    onCheckedChange = onChangeTheme,
                    switchColors = switchColors,
                    switchTestTag = "settings_theme_switch"
                )

                SettingsNavigationRow(
                    icon = Icons.Default.CurrencyExchange,
                    title = stringResource(R.string.settings_currency),
                    subtitle = uiState.selectedCurrency.localizedDisplayName(),
                    leadingBadge = uiState.selectedCurrency.symbol,
                    onClick = onOpenCurrencySelector,
                    rowTestTag = "settings_currency_row"
                )

                SettingsNavigationRow(
                    icon = Icons.Default.Key,
                    title = stringResource(R.string.settings_change_password),
                    subtitle = null,
                    onClick = onChangePasswordClicked
                )
            }

            Spacer(modifier = Modifier.size(30.dp))

            TextButton(
                onClick = onShowDeleteDialog,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = RedZoneColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.settings_delete_account),
                    color = RedZoneColor,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            confirmButton = {
                TextButton(onClick = onDeleteAccountConfirmed) {
                    Text(
                        text = stringResource(R.string.settings_delete_account_confirm),
                        color = RedZoneColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialog) {
                    Text(text = stringResource(R.string.close_snackbar_button))
                }
            },
            title = {
                Text(text = stringResource(R.string.settings_delete_account))
            },
            text = {
                Text(text = stringResource(R.string.settings_delete_account_dialog_message))
            }
        )
    }

    if (showDeleteReauthDialog) {
        AlertDialog(
            onDismissRequest = onDismissDeleteReauthDialog,
            confirmButton = {
                TextButton(
                    onClick = onDeleteAccountReauthenticated,
                    enabled = deletePassword.isNotBlank()
                ) {
                    Text(
                        text = stringResource(R.string.settings_delete_account_confirm),
                        color = RedZoneColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteReauthDialog) {
                    Text(text = stringResource(R.string.close_snackbar_button))
                }
            },
            title = {
                Text(text = stringResource(R.string.settings_delete_account_reauth_title))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = stringResource(R.string.settings_delete_account_reauth_message))
                    RedOutlinedTextField(
                        value = deletePassword,
                        textPlaceHolder = stringResource(R.string.password_placeholder),
                        leadingIcon = Icons.Default.Lock,
                        onValueChange = onDeletePasswordChanged,
                        isPassword = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                }
            }
        )
    }

    if (showCurrencySheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissCurrencySelector,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_currency),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AppCurrency.entries.forEach { currency ->
                    Card(
                        modifier = Modifier
                            .testTag("settings_currency_option_${currency.code}")
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(currency) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currency == uiState.selectedCurrency) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            } else {
                                secondarySurface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CurrencyBadge(symbol = currency.symbol)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currency.localizedDisplayName(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currency.code,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (currency == uiState.selectedCurrency) {
                                Text(
                                    text = stringResource(R.string.settings_selected_currency),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = RedZoneColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchColors: SwitchColors,
    switchTestTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsLeadingIcon(icon = icon)
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = switchColors,
            modifier = Modifier.testTag(switchTestTag)
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    leadingBadge: String? = null,
    rowTestTag: String? = null
) {
    Row(
        modifier = Modifier
            .then(
                if (rowTestTag != null) {
                    Modifier.testTag(rowTestTag)
                } else {
                    Modifier
                }
            )
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingBadge == null) {
            SettingsLeadingIcon(icon = icon)
        } else {
            CurrencyBadge(symbol = leadingBadge)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsLeadingIcon(
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RedZoneColor
        )
    }
}

@Composable
private fun CurrencyBadge(symbol: String) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = RedZoneColor
        )
    }
}

@Composable
private fun AppCurrency.localizedDisplayName(): String {
    return when (this) {
        AppCurrency.EUR -> stringResource(R.string.currency_eur)
        AppCurrency.USD -> stringResource(R.string.currency_usd)
        AppCurrency.MXN -> stringResource(R.string.currency_mxn)
        AppCurrency.GBP -> stringResource(R.string.currency_gbp)
    }
}

private fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        true
    } else {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
