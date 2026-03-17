package alejandro.developer.zonaroja.ui.common.format

import alejandro.developer.domain.models.UserPreferencesModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private val spanishLocale = Locale.forLanguageTag("es-ES")

private fun Double.toCurrencyNumberString(maxDecimals: Int): String {
    val formatter = NumberFormat.getNumberInstance(spanishLocale).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = maxDecimals
    }

    return formatter.format(this)
}

private fun convertFromEuro(
    amountInEuro: Double,
    preferences: UserPreferencesModel
): Double {
    return amountInEuro * preferences.selectedCurrency.rateFromEuro
}

fun formatCurrencyAmount(
    amountInEuro: Number,
    preferences: UserPreferencesModel,
    maxDecimals: Int = 0
): String {
    val convertedAmount = convertFromEuro(amountInEuro.toDouble(), preferences)
    return "${preferences.selectedCurrency.symbol} ${convertedAmount.toCurrencyNumberString(maxDecimals)}"
}

fun formatPricePerSquareMeter(
    amountInEuro: Number,
    preferences: UserPreferencesModel
): String {
    return "${formatCurrencyAmount(amountInEuro, preferences)}/m²"
}

fun formatCompactPricePerSquareMeter(
    amountInEuro: Float,
    preferences: UserPreferencesModel
): String {
    val convertedAmount = convertFromEuro(amountInEuro.toDouble(), preferences)

    return when {
        convertedAmount >= 10_000 -> {
            "${preferences.selectedCurrency.symbol} ${(convertedAmount / 1_000).toCurrencyNumberString(0)}K/m²"
        }

        convertedAmount >= 1_000 -> {
            "${preferences.selectedCurrency.symbol} ${(convertedAmount / 1_000).toCurrencyNumberString(1)}K/m²"
        }

        else -> {
            val decimals = if (abs(convertedAmount % 1) < 0.01) 0 else 1
            "${preferences.selectedCurrency.symbol} ${convertedAmount.toCurrencyNumberString(decimals)}/m²"
        }
    }
}
