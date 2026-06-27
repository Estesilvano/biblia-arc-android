package com.example.biblia.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblia.data.entity.Verse
import com.example.biblia.data.repository.BibleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(repository: BibleRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<List<Verse>> = _query
        .debounce(300)
        .filter { it.length >= 3 }
        .flatMapLatest { repository.searchVerses(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateQuery(q: String) {
        _query.value = q
    }
}
