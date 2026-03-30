package alejandro.developer.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

class AppCurrencyTest {

    @Test
    fun fromCode_returnsMatchingCurrency_whenCodeExists() {
        assertEquals(AppCurrency.GBP, AppCurrency.fromCode("GBP"))
    }

    @Test
    fun fromCode_returnsEuro_whenCodeDoesNotExist() {
        assertEquals(AppCurrency.EUR, AppCurrency.fromCode("UNKNOWN"))
    }
}
