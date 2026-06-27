package com.example.biblia.ui.screen.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblia.data.entity.*
import com.example.biblia.data.repository.BibleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReadingViewModel(
    private val repository: BibleRepository,
    private val bookId: Int,
    private var currentChapter: Int
) : ViewModel() {

    val book: StateFlow<Book?> = flow {
        emit(repository.getBook(bookId))
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val chapters: StateFlow<List<Int>> = repository.getChapters(bookId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val verses: StateFlow<List<Verse>> = repository.getVerses(bookId, currentChapter)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val bookmarkedIds: StateFlow<Set<Int>> = repository.bookmarkedVerseIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    private val _selectedStrong = MutableStateFlow<String?>(null)
    val selectedStrong: StateFlow<String?> = _selectedStrong.asStateFlow()

    private val _strongDetail = MutableStateFlow<Strong?>(null)
    val strongDetail: StateFlow<Strong?> = _strongDetail.asStateFlow()

    private val _commentary = MutableStateFlow<Commentary?>(null)
    val commentary: StateFlow<Commentary?> = _commentary.asStateFlow()

    private val _chapterTitle = MutableStateFlow("")
    val chapterTitle: StateFlow<String> = _chapterTitle.asStateFlow()

    init {
        viewModelScope.launch {
            book.collect { b ->
                if (b != null) {
                    _chapterTitle.value = "${b.abbreviation} $currentChapter"
                }
            }
        }
    }

    fun selectStrong(number: String) {
        _selectedStrong.value = number
        viewModelScope.launch {
            _strongDetail.value = repository.getStrong(number)
        }
    }

    fun dismissStrong() {
        _selectedStrong.value = null
        _strongDetail.value = null
    }

    fun loadCommentary(verseId: Int) {
        viewModelScope.launch {
            _commentary.value = repository.getCommentary(verseId)
        }
    }

    fun dismissCommentary() {
        _commentary.value = null
    }

    fun toggleBookmark(verseId: Int) {
        viewModelScope.launch {
            if (verseId in bookmarkedIds.value) {
                val bm = repository.bookmarks.value.find { it.verseId == verseId }
                if (bm != null) repository.removeBookmark(bm)
            } else {
                repository.addBookmark(Bookmark(verseId = verseId))
            }
        }
    }

    fun changeChapter(chapter: Int) {
        currentChapter = chapter
        _chapterTitle.value = "${book.value?.abbreviation ?: ""} $chapter"
        viewModelScope.launch {
            // triggers recomposition via verses flow
        }
    }

    val chapter: Int get() = currentChapter
}
