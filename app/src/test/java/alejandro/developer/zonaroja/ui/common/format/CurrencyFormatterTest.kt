package alejandro.developer.zonaroja.ui.common.format

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.zonaroja.sampleUserPreferencesModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun formatCurrencyAmount_convertsAndFormatsUsingSelectedCurrency() {
        val preferences = sampleUserPreferencesModel(AppCurrency.USD)

        val result = formatCurrencyAmount(amountInEuro = 100, preferences = preferences, maxDecimals = 2)

        assertEquals("${AppCurrency.USD.symbol} 109", result)
    }

    @Test
    fun formatPricePerSquareMeter_addsUnitSuffix() {
        val preferences = sampleUserPreferencesModel(AppCurrency.EUR)

        val result = formatPricePerSquareMeter(amountInEuro = 1250, preferences = preferences)

        assertTrue(result.startsWith("${AppCurrency.EUR.symbol} 1.250"))
        assertTrue(result.contains("/m"))
    }

    @Test
    fun formatCompactPricePerSquareMeter_usesCompactThousandsWhenNeeded() {
        val preferences = sampleUserPreferencesModel(AppCurrency.EUR)

        val result = formatCompactPricePerSquareMeter(
            amountInEuro = 13_000f,
            preferences = preferences
        )

        assertTrue(result.startsWith("${AppCurrency.EUR.symbol} 13K"))
        assertTrue(result.contains("/m"))
    }

    @Test
    fun formatCompactPricePerSquareMeter_keepsDecimalsForSmallValues() {
        val preferences = sampleUserPreferencesModel(AppCurrency.GBP)

        val result = formatCompactPricePerSquareMeter(
            amountInEuro = 12.5f,
            preferences = preferences,
            compactDecimals = 1,
            unitDecimals = 1
        )

        assertTrue(result.startsWith("${AppCurrency.GBP.symbol} 10,8"))
        assertTrue(result.contains("/m"))
    }
}
