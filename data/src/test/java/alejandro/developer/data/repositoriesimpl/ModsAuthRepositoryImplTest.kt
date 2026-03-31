package alejandro.developer.data.repositoriesimpl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ModsAuthRepositoryImplTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("mods_auth_session", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun loginWithEmail_persistsSessionAndMarksPasswordProvider() = runTest {
        val repository = ModsAuthRepositoryImpl(context)

        val result = repository.loginWithEmail("user@example.com", "secret1")

        assertTrue(result.isSuccess)
        assertEquals("user@example.com", repository.getCurrentUserEmail())
        assertTrue(repository.isCurrentUserPasswordProvider())
        assertTrue(repository.isUserLoggedIn())
        assertNotNull(repository.observeCurrentUserId().first())
    }

    @Test
    fun loginWithGoogle_usesMockGoogleEmailAndDisablesPasswordProvider() = runTest {
        val repository = ModsAuthRepositoryImpl(context)

        val result = repository.loginWithGoogle("id-token")

        assertTrue(result.isSuccess)
        assertEquals("mods-google@zonaroja.local", repository.getCurrentUserEmail())
        assertFalse(repository.isCurrentUserPasswordProvider())
    }

    @Test
    fun logout_clearsSession() = runTest {
        val repository = ModsAuthRepositoryImpl(context)
        repository.loginWithEmail("user@example.com", "secret1")

        repository.logout()

        assertNull(repository.getCurrentUserId())
        assertNull(repository.getCurrentUserEmail())
        assertFalse(repository.isUserLoggedIn())
    }

    @Test
    fun reauthenticateWithEmail_failsWhenThereIsNoActiveSession() = runTest {
        val repository = ModsAuthRepositoryImpl(context)

        val result = repository.reauthenticateWithEmail("user@example.com", "secret1")

        assertTrue(result.isFailure)
    }

    @Test
    fun storedSession_isLoadedWhenRepositoryIsRecreated() = runTest {
        ModsAuthRepositoryImpl(context).loginWithEmail("persisted@example.com", "secret1")

        val recreatedRepository = ModsAuthRepositoryImpl(context)

        assertEquals("persisted@example.com", recreatedRepository.getCurrentUserEmail())
        assertTrue(recreatedRepository.isUserLoggedIn())
    }
}
