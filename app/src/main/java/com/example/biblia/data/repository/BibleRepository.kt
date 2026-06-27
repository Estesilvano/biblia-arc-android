package com.example.biblia.data.repository

import com.example.biblia.data.database.dao.BibleDao
import com.example.biblia.data.database.dao.SearchResult
import com.example.biblia.data.database.entity.Book
import com.example.biblia.data.database.entity.Bookmark
import com.example.biblia.data.database.entity.Commentary
import com.example.biblia.data.database.entity.Strong
import com.example.biblia.data.database.entity.Verse
import kotlinx.coroutines.flow.Flow

class BibleRepository(private val dao: BibleDao) {
    fun getAllBooks(): Flow<List<Book>> = dao.getAllBooks()
    fun getBooksByTestament(testament: Int): Flow<List<Book>> = dao.getBooksByTestament(testament)
    suspend fun getBook(bookId: Int): Book? = dao.getBook(bookId)
    suspend fun getVersesByChapter(bookId: Int, chapter: Int): List<Verse> = dao.getVersesByChapter(bookId, chapter)
    fun getVersesByChapterFlow(bookId: Int, chapter: Int): Flow<List<Verse>> = dao.getVersesByChapterFlow(bookId, chapter)
    suspend fun getVerse(verseId: Long): Verse? = dao.getVerse(verseId)
    suspend fun searchVerses(query: String): List<Verse> = dao.searchVerses(query)

    suspend fun getStrong(strongNumber: String): Strong? = dao.getStrong(strongNumber)
    suspend fun getCommentaries(verseId: Long): List<Commentary> = dao.getCommentaries(verseId)
    suspend fun getMaxChapter(bookId: Int): Int? = dao.getMaxChapter(bookId)
    suspend fun getMaxVerse(bookId: Int, chapter: Int): Int? = dao.getMaxVerse(bookId, chapter)

    fun getAllBookmarks(): Flow<List<Bookmark>> = dao.getAllBookmarks()
    suspend fun getBookmarkForVerse(verseId: Long): Bookmark? = dao.getBookmarkForVerse(verseId)
    suspend fun addBookmark(bookmark: Bookmark) = dao.addBookmark(bookmark)
    suspend fun removeBookmark(verseId: Long) = dao.removeBookmark(verseId)
}
