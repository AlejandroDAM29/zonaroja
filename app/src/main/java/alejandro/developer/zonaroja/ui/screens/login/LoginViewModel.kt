package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.zonaroja.ui.screens.main.MainUiState
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(isLoading = true))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun getTokenAccess(){
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword("122bd08@gmail.com", "morodo2")
            .addOnSuccessListener { result ->
                result.user?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
                    val idToken = tokenResult.token
                    Log.i("test-100",idToken!!)
                }
            }

    }
}