package com.example.biblia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.biblia.data.entity.Book
import com.example.biblia.data.entity.Bookmark
import com.example.biblia.data.entity.Commentary
import com.example.biblia.data.entity.Strong
import com.example.biblia.data.entity.Verse
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM books ORDER BY id")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBook(id: Int): Book?

    @Query("SELECT * FROM verses WHERE book_id = :bookId AND chapter = :chapter ORDER BY verse")
    fun getVerses(bookId: Int, chapter: Int): Flow<List<Verse>>

    @Query("SELECT DISTINCT chapter FROM verses WHERE book_id = :bookId ORDER BY chapter")
    fun getChapters(bookId: Int): Flow<List<Int>>

    @Query("SELECT * FROM strongs WHERE number = :number")
    suspend fun getStrong(number: String): Strong?

    @Query("SELECT * FROM commentaries WHERE verse_id = :verseId LIMIT 1")
    suspend fun getCommentary(verseId: Int): Commentary?

    @Query("SELECT * FROM verses WHERE text LIKE '%' || :query || '%' LIMIT 80")
    fun searchVerses(query: String): Flow<List<Verse>>

    @Query("SELECT * FROM verses WHERE id = :id")
    suspend fun getVerseById(id: Int): Verse?

    @Insert
    suspend fun addBookmark(bookmark: Bookmark)

    @Delete
    suspend fun removeBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    fun getBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT verse_id FROM bookmarks")
    fun getBookmarkedVerseIds(): Flow<List<Int>>
}
