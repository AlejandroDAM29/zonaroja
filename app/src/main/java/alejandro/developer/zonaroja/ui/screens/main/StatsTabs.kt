package alejandro.developer.zonaroja.ui.screens.main

sealed class StatsTab(val title: String) {

    object Economy : StatsTab("Economía")
    object Society : StatsTab("Sociedad")
    object Demography : StatsTab("Demografía")

    companion object {
        val allChartMapTabs = listOf(Economy, Society, Demography)
    }
}