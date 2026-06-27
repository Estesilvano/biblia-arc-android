package com.example.biblia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.biblia.data.repository.BibleRepository
import com.example.biblia.ui.screen.home.HomeScreen
import com.example.biblia.ui.screen.home.HomeViewModelFactory
import com.example.biblia.ui.screen.reading.ReadingScreen
import com.example.biblia.ui.screen.reading.ReadingViewModelFactory
import com.example.biblia.ui.screen.search.SearchScreen
import com.example.biblia.ui.screen.search.SearchViewModelFactory
import com.example.biblia.ui.screen.settings.SettingsScreen
import com.example.biblia.util.SettingsManager

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    repository: BibleRepository,
    settingsManager: SettingsManager,
    navController: NavHostController = rememberNavController()
) {
    val homeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    val searchViewModel = viewModel(factory = SearchViewModelFactory(repository))

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onBookClick = { bookId -> navController.navigate("reading/$bookId/1") },
                onSettingsClick = { navController.navigate("settings") },
                onSearchClick = { navController.navigate("search") }
            )
        }
        composable(
            "reading/{bookId}/{chapter}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType },
                navArgument("chapter") { type = NavType.IntType }
            )
        ) {
            val bookId = it.arguments?.getInt("bookId") ?: 1
            val chapter = it.arguments?.getInt("chapter") ?: 1
            val readingViewModel = viewModel(
                key = "$bookId-$chapter",
                factory = ReadingViewModelFactory(repository, bookId, chapter)
            )
            ReadingScreen(
                viewModel = readingViewModel,
                onBack = { navController.popBackStack() },
                onChapterChange = { newChapter ->
                    navController.popBackStack()
                    navController.navigate("reading/$bookId/$newChapter")
                },
                onHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
            )
        }
        composable("search") {
            SearchScreen(
                viewModel = searchViewModel,
                onBack = { navController.popBackStack() },
                onVerseClick = { bookId, chapter ->
                    navController.navigate("reading/$bookId/$chapter") {
                        popUpTo("home")
                    }
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                settingsManager = settingsManager,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
