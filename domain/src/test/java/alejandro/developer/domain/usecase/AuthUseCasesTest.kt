package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthUseCasesTest {

    private val repository: AuthRepository = mock()

    @Test
    fun checkUserSession_returnsRepositoryValue() {
        whenever(repository.isUserLoggedIn()).thenReturn(true)

        val result = CheckUserSessionUseCase(repository).invoke()

        assertTrue(result)
        verify(repository).isUserLoggedIn()
    }

    @Test
    fun getCurrentUserEmail_returnsRepositoryValue() {
        whenever(repository.getCurrentUserEmail()).thenReturn("user@example.com")

        val result = GetCurrentUserEmailUseCase(repository).invoke()

        assertEquals("user@example.com", result)
        verify(repository).getCurrentUserEmail()
    }

    @Test
    fun isCurrentUserPasswordProvider_returnsRepositoryValue() {
        whenever(repository.isCurrentUserPasswordProvider()).thenReturn(false)

        val result = IsCurrentUserPasswordProviderUseCase(repository).invoke()

        assertFalse(result)
        verify(repository).isCurrentUserPasswordProvider()
    }

    @Test
    fun loginWithEmail_delegatesToRepository() = runTest {
        val expected = Result.success(Unit)
        whenever(repository.loginWithEmail("user@example.com", "secret1")).thenReturn(expected)

        val result = LoginWithEmailUseCase(repository).invoke("user@example.com", "secret1")

        assertEquals(expected, result)
        verify(repository).loginWithEmail("user@example.com", "secret1")
    }

    @Test
    fun loginWithGoogle_delegatesToRepository() = runTest {
        val expected = Result.success(Unit)
        whenever(repository.loginWithGoogle("token")).thenReturn(expected)

        val result = LoginWithGoogleUseCase(repository).invoke("token")

        assertEquals(expected, result)
        verify(repository).loginWithGoogle("token")
    }

    @Test
    fun registerWithEmail_delegatesToRepository() = runTest {
        val expected = Result.success(Unit)
        whenever(repository.registerWithEmail("user@example.com", "secret1")).thenReturn(expected)

        val result = RegisterWithEmailUseCase(repository).invoke("user@example.com", "secret1")

        assertEquals(expected, result)
        verify(repository).registerWithEmail("user@example.com", "secret1")
    }

    @Test
    fun sendPasswordResetEmail_delegatesToRepository() = runTest {
        val expected = Result.success(Unit)
        whenever(repository.sendPasswordResetEmail("user@example.com")).thenReturn(expected)

        val result = SendPasswordResetEmailUseCase(repository).invoke("user@example.com")

        assertEquals(expected, result)
        verify(repository).sendPasswordResetEmail("user@example.com")
    }

    @Test
    fun reauthenticateWithEmail_delegatesToRepository() = runTest {
        val expected = Result.success(Unit)
        whenever(repository.reauthenticateWithEmail("user@example.com", "secret1")).thenReturn(expected)

        val result = ReauthenticateWithEmailUseCase(repository).invoke("user@example.com", "secret1")

        assertEquals(expected, result)
        verify(repository).reauthenticateWithEmail("user@example.com", "secret1")
    }

    @Test
    fun deleteCurrentUser_delegatesToRepository() = runTest {
        val expected = Result.success(Unit)
        whenever(repository.deleteCurrentUser()).thenReturn(expected)

        val result = DeleteCurrentUserUseCase(repository).invoke()

        assertEquals(expected, result)
        verify(repository).deleteCurrentUser()
    }

    @Test
    fun logout_delegatesToRepository() = runTest {
        LogoutUseCase(repository).invoke()

        verify(repository).logout()
    }
}
