package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.NotificationDetail
import alejandro.developer.zonaroja.navigation.Notifications
import alejandro.developer.zonaroja.ui.screens.notificationdetail.NotificationDetailScreen
import alejandro.developer.zonaroja.ui.screens.notifications.NotificationsScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.notificationsNavGraph(
    navController: NavController
) {
    navigation<NotificationsGraph>(startDestination = Notifications) {
        composable<Notifications> {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onOpenNotification = { notificationId ->
                    navController.navigate(NotificationDetail(notificationId))
                }
            )
        }

        composable<NotificationDetail> { backStackEntry ->
            val args: NotificationDetail = backStackEntry.toRoute()

            NotificationDetailScreen(
                notificationId = args.notificationId,
                onClose = { navController.popBackStack() }
            )
        }
    }
}
