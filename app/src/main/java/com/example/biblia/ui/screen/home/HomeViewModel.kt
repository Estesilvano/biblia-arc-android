package com.example.biblia.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblia.data.entity.Book
import com.example.biblia.data.repository.BibleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(repository: BibleRepository) : ViewModel() {
    val books: StateFlow<List<Book>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val oldTestament: StateFlow<List<Book>> = books.map { list ->
        list.filter { it.testament == 0 }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val newTestament: StateFlow<List<Book>> = books.map { list ->
        list.filter { it.testament == 1 }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
