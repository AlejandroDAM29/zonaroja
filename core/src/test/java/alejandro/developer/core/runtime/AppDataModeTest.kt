package alejandro.developer.core.runtime

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDataModeTest {

    @Test
    fun usesModsData_isTrueOnlyForModsMode() {
        assertFalse(AppDataMode.BACKEND.usesModsData)
        assertTrue(AppDataMode.MODS.usesModsData)
    }
}
