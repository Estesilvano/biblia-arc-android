package com.example.biblia.ui.screen.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biblia.data.repository.BibleRepository

class ReadingViewModelFactory(
    private val repository: BibleRepository,
    private val bookId: Int,
    private val chapter: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReadingViewModel(repository, bookId, chapter) as T
    }
}
