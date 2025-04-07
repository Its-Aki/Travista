import androidx.navigation.NavController
import androidx.navigation.NavHostController


fun navigateToTabStartDestination(
    navController: NavHostController,
    startDestination: String, // e.g. "explore_screen"
    rootRoute: String // e.g. "explore"
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (currentRoute == startDestination || currentRoute == rootRoute) return

    val hasBackStackEntry = try {
        navController.getBackStackEntry(rootRoute)
        true
    } catch (e: IllegalArgumentException) {
        false
    }

    navController.navigate(rootRoute) {
        if (hasBackStackEntry) {
            popUpTo(rootRoute) {
                inclusive = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
