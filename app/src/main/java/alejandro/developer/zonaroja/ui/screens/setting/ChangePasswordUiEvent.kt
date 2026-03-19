package alejandro.developer.zonaroja.ui.screens.setting

sealed interface ChangePasswordUiEvent {
    data class ShowMessage(
        val messageRes: Int,
        val type: SettingMessageType
    ) : ChangePasswordUiEvent
}
