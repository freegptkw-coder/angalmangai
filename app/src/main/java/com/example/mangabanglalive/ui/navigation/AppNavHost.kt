package com.example.mangabanglalive.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mangabanglalive.R
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
import kotlinx.coroutines.delay

@Composable
fun AppNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val factory = remember { AppViewModelFactory(container) }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
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

@Composable
private fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (visible) 1f else 0.8f, label = "splash-scale")
    val alpha by animateFloatAsState(if (visible) 1f else 0f, label = "splash-alpha")

    LaunchedEffect(Unit) {
        visible = true
        delay(900)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale).alpha(alpha)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Manga Bangla Live", style = MaterialTheme.typography.titleLarge)
        }
    }
}
