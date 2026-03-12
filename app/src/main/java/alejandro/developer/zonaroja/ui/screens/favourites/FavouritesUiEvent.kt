package alejandro.developer.zonaroja.ui.screens.favourites

sealed interface FavouritesUiEvent {
    data class ShowError(val message: String) : FavouritesUiEvent
}
