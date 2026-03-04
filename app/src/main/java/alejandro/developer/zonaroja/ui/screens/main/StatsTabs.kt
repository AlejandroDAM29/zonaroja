package alejandro.developer.zonaroja.ui.screens.main

sealed class StatsTab(val title: String) {

    object Economy : StatsTab("Economía")
    object Housing : StatsTab("Vivienda")
    object Demography : StatsTab("Demografía")

    companion object {
        val allChartMapTabs = listOf(Economy, Housing, Demography)
    }
}