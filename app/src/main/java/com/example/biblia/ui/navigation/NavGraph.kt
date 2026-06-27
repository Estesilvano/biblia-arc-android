package com.example.biblia.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Reading : Screen("reading/{bookId}/{bookName}/{bookAbbr}/{chapter}") {
        fun createRoute(bookId: Int, bookName: String, bookAbbr: String, chapter: Int): String {
            return "reading/$bookId/$bookName/$bookAbbr/$chapter"
        }
    }
    data object Search : Screen("search")
    data object Settings : Screen("settings")
}
