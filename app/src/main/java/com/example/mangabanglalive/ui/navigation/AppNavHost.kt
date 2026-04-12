package com.example.mangabanglalive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mangabanglalive.app.AppContainer
import com.example.mangabanglalive.app.AppViewModelFactory
import com.example.mangabanglalive.browser.BrowserScreen
import com.example.mangabanglalive.browser.BrowserViewModel
import com.example.mangabanglalive.reader.ReaderScreen
import com.example.mangabanglalive.reader.ReaderViewModel
import com.example.mangabanglalive.settings.SettingsScreen
import com.example.mangabanglalive.settings.SettingsViewModel
import com.example.mangabanglalive.ui.home.HomeScreen
import com.example.mangabanglalive.ui.home.HomeViewModel
import com.example.mangabanglalive.ui.projects.ProjectsScreen
import com.example.mangabanglalive.ui.projects.ProjectsViewModel

@Composable
fun AppNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val factory = remember { AppViewModelFactory(container) }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = viewModel,
                onOpenProject = { navController.navigate("${Routes.READER}/$it") },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenProjects = { navController.navigate(Routes.PROJECTS) },
                onOpenBrowser = { navController.navigate(Routes.BROWSER) }
            )
        }
        composable(
            route = "${Routes.READER}/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId").orEmpty()
            val viewModel: ReaderViewModel = viewModel(factory = factory)
            ReaderScreen(projectId = projectId, viewModel = viewModel) { navController.popBackStack() }
        }
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = viewModel(factory = factory)
            SettingsScreen(viewModel = viewModel) { navController.popBackStack() }
        }
        composable(Routes.PROJECTS) {
            val viewModel: ProjectsViewModel = viewModel(factory = factory)
            ProjectsScreen(
                viewModel = viewModel,
                onOpenProject = { navController.navigate("${Routes.READER}/$it") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.BROWSER) {
            val viewModel: BrowserViewModel = viewModel(factory = factory)
            BrowserScreen(viewModel = viewModel) { navController.popBackStack() }
        }
    }
}