package com.example.biblia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biblia.ui.BibliaViewModel
import com.example.biblia.ui.screens.home.HomeScreen
import com.example.biblia.ui.screens.reading.ReadingScreen
import com.example.biblia.ui.screens.search.SearchScreen
import com.example.biblia.ui.screens.settings.SettingsScreen
import com.example.biblia.ui.theme.BibliaTheme

sealed class AppScreen {
    data object Home : AppScreen()
    data class Reading(
        val bookId: Int,
        val bookName: String,
        val bookAbbr: String,
        val chapter: Int
    ) : AppScreen()
    data object Search : AppScreen()
    data object Settings : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BibliaViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val fontSize by viewModel.fontSize.collectAsState()
            val lineSpacing by viewModel.lineSpacing.collectAsState()
            val verses by viewModel.verses.collectAsState()
            val selectedVerse by viewModel.selectedVerse.collectAsState()
            val commentaries by viewModel.commentaries.collectAsState()
            val selectedBook by viewModel.selectedBook.collectAsState()
            val selectedChapter by viewModel.selectedChapter.collectAsState()
            val maxChapter by viewModel.maxChapter.collectAsState()
            val currentBookAbbr by viewModel.currentBookAbbr.collectAsState()

            var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

            BibliaTheme(themeMode = themeMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier.padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            is AppScreen.Home -> HomeScreen(
                                booksFlow = viewModel.getAllBooks(),
                                onBookClick = { book ->
                                    viewModel.selectBook(book)
                                    currentScreen = AppScreen.Reading(
                                        bookId = book.id,
                                        bookName = book.name,
                                        bookAbbr = book.abbreviation,
                                        chapter = 1
                                    )
                                },
                                onSearchClick = { currentScreen = AppScreen.Search }
                            )

                            is AppScreen.Reading -> ReadingScreen(
                                bookName = screen.bookName,
                                bookAbbr = screen.bookAbbr,
                                chapter = selectedChapter,
                                maxChapter = maxChapter,
                                verses = verses,
                                fontSize = fontSize,
                                lineSpacing = lineSpacing,
                                selectedVerse = selectedVerse,
                                commentaries = commentaries,
                                onBack = { currentScreen = AppScreen.Home },
                                onChapterSelect = { viewModel.selectChapter(it) },
                                onVerseClick = { viewModel.selectVerse(it) },
                                onCloseVerse = { viewModel.clearSelectedVerse() },
                                onPreviousChapter = { viewModel.previousChapter() },
                                onNextChapter = { viewModel.nextChapter() },
                                onToggleBookmark = { viewModel.toggleBookmark(it) },
                                onSettingsClick = { currentScreen = AppScreen.Settings }
                            )

                            is AppScreen.Search -> SearchScreen(
                                onBack = { currentScreen = AppScreen.Home },
                                onVerseClick = { verse ->
                                    viewModel.selectBook(
                                        com.example.biblia.data.database.entity.Book(
                                            id = verse.bookId,
                                            name = "",
                                            abbreviation = "",
                                            testament = 0,
                                            bookOrder = 0
                                        )
                                    )
                                }
                            )

                            is AppScreen.Settings -> SettingsScreen(
                                fontSize = fontSize,
                                lineSpacing = lineSpacing,
                                themeMode = themeMode,
                                onFontSizeChange = { viewModel.setFontSize(it) },
                                onLineSpacingChange = { viewModel.setLineSpacing(it) },
                                onThemeModeChange = { viewModel.setThemeMode(it) },
                                onBack = {
                                    val book = selectedBook
                                    if (book != null) {
                                        currentScreen = AppScreen.Reading(
                                            bookId = book.id,
                                            bookName = book.name,
                                            bookAbbr = book.abbreviation,
                                            chapter = selectedChapter
                                        )
                                    } else {
                                        currentScreen = AppScreen.Home
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
