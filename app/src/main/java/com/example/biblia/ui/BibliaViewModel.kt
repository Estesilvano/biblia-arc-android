package com.example.biblia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblia.BibliaApp
import com.example.biblia.data.database.entity.Book
import com.example.biblia.data.database.entity.Bookmark
import com.example.biblia.data.database.entity.Commentary
import com.example.biblia.data.database.entity.Strong
import com.example.biblia.data.database.entity.Verse
import com.example.biblia.data.repository.BibleRepository
import com.example.biblia.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BibliaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BibleRepository((application as BibliaApp).database.bibleDao())

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    private val _selectedChapter = MutableStateFlow(1)
    val selectedChapter: StateFlow<Int> = _selectedChapter.asStateFlow()

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses: StateFlow<List<Verse>> = _verses.asStateFlow()

    private val _selectedVerse = MutableStateFlow<Verse?>(null)
    val selectedVerse: StateFlow<Verse?> = _selectedVerse.asStateFlow()

    private val _commentaries = MutableStateFlow<List<Commentary>>(emptyList())
    val commentaries: StateFlow<List<Commentary>> = _commentaries.asStateFlow()

    private val _fontSize = MutableStateFlow(18f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _lineSpacing = MutableStateFlow(1.6f)
    val lineSpacing: StateFlow<Float> = _lineSpacing.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _maxChapter = MutableStateFlow(1)
    val maxChapter: StateFlow<Int> = _maxChapter.asStateFlow()

    private val _currentBookAbbr = MutableStateFlow("")
    val currentBookAbbr: StateFlow<String> = _currentBookAbbr.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getAllBooks(): Flow<List<Book>> = repository.getAllBooks()
    fun getBooksByTestament(testament: Int): Flow<List<Book>> = repository.getBooksByTestament(testament)

    fun selectBook(book: Book) {
        _selectedBook.value = book
        _currentBookAbbr.value = book.abbreviation
        _selectedChapter.value = 1
        loadChapter()
    }

    fun selectChapter(chapter: Int) {
        _selectedChapter.value = chapter
        loadChapter()
    }

    private fun loadChapter() {
        val book = _selectedBook.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _verses.value = repository.getVersesByChapter(book.id, _selectedChapter.value)
            _maxChapter.value = repository.getMaxChapter(book.id) ?: 1
            _isLoading.value = false
        }
    }

    fun selectVerse(verse: Verse) {
        _selectedVerse.value = verse
        viewModelScope.launch {
            _commentaries.value = repository.getCommentaries(verse.id)
        }
    }

    fun clearSelectedVerse() {
        _selectedVerse.value = null
        _commentaries.value = emptyList()
    }

    fun nextChapter() {
        val max = _maxChapter.value
        val current = _selectedChapter.value
        if (current < max) {
            selectChapter(current + 1)
        }
    }

    fun previousChapter() {
        val current = _selectedChapter.value
        if (current > 1) {
            selectChapter(current - 1)
        }
    }

    fun searchVerses(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _verses.value = repository.searchVerses(query)
            _isLoading.value = false
        }
    }

    fun getVerse(verseId: Long) {
        viewModelScope.launch {
            val v = repository.getVerse(verseId)
            if (v != null) {
                val book = repository.getBook(v.bookId)
                if (book != null) {
                    _selectedBook.value = book
                    _currentBookAbbr.value = book.abbreviation
                    _selectedChapter.value = v.chapter
                    loadChapter()
                }
            }
        }
    }

    fun setFontSize(size: Float) { _fontSize.value = size }
    fun setLineSpacing(spacing: Float) { _lineSpacing.value = spacing }
    fun setThemeMode(mode: ThemeMode) { _themeMode.value = mode }

    fun getStrong(number: String) {
        viewModelScope.launch {
            val strong = repository.getStrong(number)
        }
    }

    fun getAllBookmarks(): Flow<List<Bookmark>> = repository.getAllBookmarks()

    fun toggleBookmark(verseId: Long) {
        viewModelScope.launch {
            val existing = repository.getBookmarkForVerse(verseId)
            if (existing != null) {
                repository.removeBookmark(verseId)
            } else {
                repository.addBookmark(
                    Bookmark(
                        verseId = verseId,
                        type = "bookmark",
                        color = null,
                        note = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun getBookmarkForVerse(verseId: Long) {
        viewModelScope.launch {
            repository.getBookmarkForVerse(verseId)
        }
    }
}
