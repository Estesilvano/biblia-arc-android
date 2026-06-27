package com.example.biblia.data.repository

import com.example.biblia.data.dao.BibleDao
import com.example.biblia.data.entity.Book
import com.example.biblia.data.entity.Bookmark
import com.example.biblia.data.entity.Commentary
import com.example.biblia.data.entity.Strong
import com.example.biblia.data.entity.Verse
import kotlinx.coroutines.flow.Flow

class BibleRepository(private val dao: BibleDao) {
    val allBooks: Flow<List<Book>> = dao.getAllBooks()

    fun getVerses(bookId: Int, chapter: Int): Flow<List<Verse>> =
        dao.getVerses(bookId, chapter)

    fun getChapters(bookId: Int): Flow<List<Int>> =
        dao.getChapters(bookId)

    suspend fun getStrong(number: String): Strong? =
        dao.getStrong(number)

    suspend fun getCommentary(verseId: Int): Commentary? =
        dao.getCommentary(verseId)

    fun searchVerses(query: String): Flow<List<Verse>> =
        dao.searchVerses(query)

    suspend fun getVerseById(id: Int): Verse? =
        dao.getVerseById(id)

    suspend fun getBook(id: Int): Book? =
        dao.getBook(id)

    suspend fun addBookmark(bookmark: Bookmark) =
        dao.addBookmark(bookmark)

    suspend fun removeBookmark(bookmark: Bookmark) =
        dao.removeBookmark(bookmark)

    val bookmarks: Flow<List<Bookmark>> = dao.getBookmarks()

    val bookmarkedVerseIds: Flow<List<Int>> = dao.getBookmarkedVerseIds()
}
