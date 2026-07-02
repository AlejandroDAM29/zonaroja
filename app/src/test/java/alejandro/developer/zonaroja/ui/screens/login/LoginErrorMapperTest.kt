package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.network.NoInternetException
import alejandro.developer.zonaroja.R
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.GetCredentialUnknownException
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
            mapGoogleLoginErrorToStringRes(GoogleIdTokenParsingException(Throwable("bad token")))
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

    @Test
    fun emailLogin_mapsInvalidUser_toInvalidCredentialsMessage() {
        val throwable = FirebaseAuthInvalidUserException(
            "ERROR_USER_NOT_FOUND",
            "User not found"
        )

        assertEquals(
            R.string.error_auth_invalid_credentials,
            mapEmailLoginErrorToStringRes(throwable)
        )
    }

    @Test
    fun googleLogin_mapsKnownFirebaseErrorCode_toDedicatedMessage() {
        val throwable = FirebaseAuthException(
            "ERROR_EMAIL_ALREADY_IN_USE",
            "Account already exists"
        )

        assertEquals(
            R.string.error_auth_google_account_exists,
            mapGoogleLoginErrorToStringRes(throwable)
        )
    }

    @Test
    fun googleLogin_mapsCredentialErrors_toGenericGoogleMessage() {
        val throwable = GetCredentialUnknownException()

        assertEquals(
            R.string.error_auth_google_generic,
            mapGoogleLoginErrorToStringRes(throwable)
        )
    }

    @Test
    fun googleLogin_mapsMissingDeviceAccount_toDedicatedMessage() {
        assertEquals(
            R.string.error_auth_google_no_account,
            mapGoogleLoginErrorToStringRes(NoCredentialException())
        )
    }

    @Test
    fun isGoogleLoginCancellation_detectsCancellationExceptions() {
        assertTrue(isGoogleLoginCancellation(GetCredentialCancellationException()))
        assertFalse(isGoogleLoginCancellation(GetCredentialUnknownException()))
    }
}
