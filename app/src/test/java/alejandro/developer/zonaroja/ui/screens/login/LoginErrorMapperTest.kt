package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.network.NoInternetException
import alejandro.developer.zonaroja.R
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginErrorMapperTest {

    @Test
    fun emailLogin_mapsInvalidCredentials_toInvalidCredentialsMessage() {
        val throwable = FirebaseAuthInvalidCredentialsException(
            "ERROR_INVALID_CREDENTIAL",
            "Bad credentials"
        )

        assertEquals(
            R.string.error_auth_invalid_credentials,
            mapEmailLoginErrorToStringRes(throwable)
        )
    }

    @Test
    fun googleLogin_mapsNetworkFailure_toNetworkMessage() {
        assertEquals(
            R.string.error_auth_network,
            mapGoogleLoginErrorToStringRes(NoInternetException())
        )
    }

    @Test
    fun googleLogin_mapsParsingFailure_toResponseMessage() {
        assertEquals(
            R.string.error_auth_google_response,
            mapGoogleLoginErrorToStringRes(GoogleIdTokenParsingException())
        )
    }

    @Test
    fun googleLogin_mapsUserCollision_toDedicatedMessage() {
        val throwable = FirebaseAuthUserCollisionException(
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL",
            "Account already exists"
        )

        assertEquals(
            R.string.error_auth_google_account_exists,
            mapGoogleLoginErrorToStringRes(throwable)
        )
    }
}
