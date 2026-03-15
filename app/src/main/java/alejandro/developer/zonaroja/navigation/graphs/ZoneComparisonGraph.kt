package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.ZoneComparisonResult
import alejandro.developer.zonaroja.navigation.ZoneComparisonSelector
import alejandro.developer.zonaroja.ui.screens.comparisonresults.ComparisonResultScreen
import alejandro.developer.zonaroja.ui.screens.comparisonselector.ComparisonSelectorScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.zoneComparisonNavGraph(
    navController: NavController
) {
    navigation<ZoneComparisonGraph>(startDestination = ZoneComparisonSelector) {
        composable<ZoneComparisonSelector> {
            ComparisonSelectorScreen(
                onNavigateToResult = { firstZoneId, secondZoneId ->
                    navController.navigate(
                        ZoneComparisonResult(
                            firstZoneId = firstZoneId,
                            secondZoneId = secondZoneId
                        )
                    )
                }
            )
        }

        composable<ZoneComparisonResult> { backStackEntry ->
            val args: ZoneComparisonResult = backStackEntry.toRoute()

            ComparisonResultScreen(
                firstZoneId = args.firstZoneId,
                secondZoneId = args.secondZoneId,
                onBack = { navController.popBackStack() },
                onClose = { navController.popBackStack() }
            )
        }
    }
}
