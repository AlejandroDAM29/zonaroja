package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.zonaroja.R
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

private val googleAccountExistsErrorCodes = setOf(
    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL",
    "ERROR_CREDENTIAL_ALREADY_IN_USE",
    "ERROR_EMAIL_ALREADY_IN_USE"
)

internal fun mapEmailLoginErrorToStringRes(throwable: Throwable): Int {
    return when {
        throwable.isNetworkConnectivityError() -> R.string.error_auth_network
        throwable is FirebaseAuthInvalidCredentialsException -> R.string.error_auth_invalid_credentials
        throwable is FirebaseAuthInvalidUserException -> R.string.error_auth_invalid_credentials
        else -> R.string.error_auth_generic
    }
}

internal fun mapGoogleLoginErrorToStringRes(throwable: Throwable): Int {
    val sharedAuthMessage = mapEmailLoginErrorToStringRes(throwable)
    if (sharedAuthMessage != R.string.error_auth_generic) {
        return sharedAuthMessage
    }

    return when {
        throwable is FirebaseAuthUserCollisionException ->
            R.string.error_auth_google_account_exists

        throwable is FirebaseAuthException &&
            throwable.errorCode in googleAccountExistsErrorCodes ->
            R.string.error_auth_google_account_exists

        throwable is GoogleIdTokenParsingException ->
            R.string.error_auth_google_response

        throwable is IllegalStateException ->
            R.string.error_auth_google_response

        throwable is NoCredentialException ->
            R.string.error_auth_google_no_account

        throwable is GetCredentialException ->
            R.string.error_auth_google_generic

        else -> R.string.error_auth_google_generic
    }
}

internal fun isGoogleLoginCancellation(throwable: Throwable): Boolean {
    return throwable is GetCredentialException &&
        throwable::class.java.simpleName.contains("Cancellation", ignoreCase = true)
}
