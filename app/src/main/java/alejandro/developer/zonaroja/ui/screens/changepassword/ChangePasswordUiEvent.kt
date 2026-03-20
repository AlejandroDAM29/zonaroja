package alejandro.developer.zonaroja.ui.screens.changepassword

import alejandro.developer.zonaroja.ui.screens.setting.SettingMessageType

sealed interface ChangePasswordUiEvent {
    data class ShowMessage(
        val messageRes: Int,
        val type: SettingMessageType
    ) : ChangePasswordUiEvent
}
