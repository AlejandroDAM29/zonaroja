package alejandro.developer.zonaroja.ui.common.format

import alejandro.developer.domain.models.UserPreferencesModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private fun currentLocale(): Locale = Locale.getDefault()

private fun Double.toCurrencyNumberString(maxDecimals: Int): String {
    val formatter = NumberFormat.getNumberInstance(currentLocale()).apply {
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
    return "${formatCurrencyAmount(amountInEuro, preferences)}/m\u00B2"
}

fun formatCompactPricePerSquareMeter(
    amountInEuro: Float,
    preferences: UserPreferencesModel,
    compactDecimals: Int = 1,
    unitDecimals: Int = 1,
    symbolOverride: String? = null
): String {
    val convertedAmount = convertFromEuro(amountInEuro.toDouble(), preferences)
    val symbol = symbolOverride ?: preferences.selectedCurrency.symbol

    return when {
        convertedAmount >= 10_000 -> {
            "$symbol ${(convertedAmount / 1_000).toCurrencyNumberString(0)}K/m\u00B2"
        }

        convertedAmount >= 1_000 -> {
            "$symbol ${(convertedAmount / 1_000).toCurrencyNumberString(compactDecimals)}K/m\u00B2"
        }

        else -> {
            val decimals = if (abs(convertedAmount % 1) < 0.01) 0 else unitDecimals
            "$symbol ${convertedAmount.toCurrencyNumberString(decimals)}/m\u00B2"
        }
    }
}
