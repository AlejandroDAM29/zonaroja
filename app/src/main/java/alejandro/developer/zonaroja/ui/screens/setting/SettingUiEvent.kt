package alejandro.developer.zonaroja.ui.screens.setting

sealed interface SettingUiEvent {
    data class ShowMessage(
        val messageRes: Int,
        val type: SettingMessageType
    ) : SettingUiEvent

    data object RequestDeleteAccountReauthentication : SettingUiEvent

    data object NavigateToLogin : SettingUiEvent

    data class NavigateToLoginLogoutSuccess(
        val messageRes: Int
    ) : SettingUiEvent
}

enum class SettingMessageType {
    SUCCESS,
    ERROR,
    WARNING
}
