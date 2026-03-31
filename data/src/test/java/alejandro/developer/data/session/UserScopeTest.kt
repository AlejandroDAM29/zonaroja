package alejandro.developer.data.session

import org.junit.Assert.assertEquals
import org.junit.Test

class UserScopeTest {

    @Test
    fun toUserScopeKey_returnsGuestForNullUser() {
        assertEquals(GUEST_USER_SCOPE, (null as String?).toUserScopeKey())
    }

    @Test
    fun toUserScopeKey_returnsSameValueForAuthenticatedUser() {
        assertEquals("user-99", "user-99".toUserScopeKey())
    }
}
