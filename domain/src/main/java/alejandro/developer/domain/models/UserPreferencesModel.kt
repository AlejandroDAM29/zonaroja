package alejandro.developer.domain.models

enum class AppCurrency(
    val code: String,
    val displayName: String,
    val symbol: String,
    val rateFromEuro: Double
) {
    EUR(
        code = "EUR",
        displayName = "Euro",
        symbol = "\u20AC",
        rateFromEuro = 1.0
    ),
    USD(
        code = "USD",
        displayName = "Dolar estadounidense",
        symbol = "US$",
        rateFromEuro = 1.09
    ),
    MXN(
        code = "MXN",
        displayName = "Peso mexicano",
        symbol = "MX$",
        rateFromEuro = 18.45
    ),
    GBP(
        code = "GBP",
        displayName = "Libra esterlina",
        symbol = "\u00A3",
        rateFromEuro = 0.86
    );

    companion object {
        fun fromCode(code: String): AppCurrency {
            return entries.firstOrNull { it.code == code } ?: EUR
        }
    }
}

data class UserPreferencesModel(
    val darkThemeEnabled: Boolean = false,
    val selectedCurrency: AppCurrency = AppCurrency.EUR,
    val notificationsEnabled: Boolean = false
)
