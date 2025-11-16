package com.notesbible.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.ui.reader.ReaderRoute
import com.notesbible.app.ui.version.VersionListRoute

@Composable
fun NotesBibleNavHost(
    repository: BibleRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Versions.route,
        modifier = modifier
    ) {
        composable(Screen.Versions.route) {
            VersionListRoute(
                repository = repository,
                onReadVersion = { versionId ->
                    navController.navigate(Screen.Reader.createRoute(versionId))
                }
            )
        }
        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument(Screen.Reader.VERSION_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val versionId = backStackEntry.arguments?.getString(Screen.Reader.VERSION_ARG).orEmpty()
            ReaderRoute(
                repository = repository,
                versionId = versionId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Versions : Screen("versions")

    data object Reader : Screen("reader/{versionId}") {
        const val VERSION_ARG = "versionId"
        fun createRoute(versionId: String) = "reader/$versionId"
    }
}
