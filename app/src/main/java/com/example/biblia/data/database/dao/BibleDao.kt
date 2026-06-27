package com.example.biblia.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.biblia.data.database.entity.Book
import com.example.biblia.data.database.entity.Bookmark
import com.example.biblia.data.database.entity.Commentary
import com.example.biblia.data.database.entity.Strong
import com.example.biblia.data.database.entity.Verse
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM books ORDER BY book_order")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE testament = :testament ORDER BY book_order")
    fun getBooksByTestament(testament: Int): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBook(bookId: Int): Book?

    @Query("SELECT * FROM verses WHERE book_id = :bookId ORDER BY chapter, verse")
    suspend fun getVersesByBook(bookId: Int): List<Verse>

    @Query("SELECT * FROM verses WHERE book_id = :bookId AND chapter = :chapter ORDER BY verse")
    suspend fun getVersesByChapter(bookId: Int, chapter: Int): List<Verse>

    @Query("SELECT * FROM verses WHERE book_id = :bookId AND chapter = :chapter ORDER BY verse")
    fun getVersesByChapterFlow(bookId: Int, chapter: Int): Flow<List<Verse>>

    @Query("SELECT * FROM verses WHERE id = :verseId")
    suspend fun getVerse(verseId: Long): Verse?

    @Query("""
        SELECT v.* FROM verses v
        JOIN books b ON v.book_id = b.id
        WHERE v.text LIKE '%' || :query || '%'
        ORDER BY b.book_order, v.chapter, v.verse
        LIMIT 50
    """)
    suspend fun searchVerses(query: String): List<Verse>

    @Query("""
        SELECT DISTINCT b.id, b.name, b.abbreviation, b.testament, b.book_order,
               v.chapter, v.verse, v.text
        FROM verses v
        JOIN books b ON v.book_id = b.id
        WHERE v.text LIKE '%' || :query || '%'
        ORDER BY b.book_order, v.chapter, v.verse
        LIMIT 50
    """)
    suspend fun searchWithBook(query: String): List<SearchResult>

    @Query("SELECT * FROM strongs WHERE number = :strongNumber")
    suspend fun getStrong(strongNumber: String): Strong?

    @Query("SELECT * FROM strongs WHERE number LIKE :prefix || '%'")
    suspend fun getStrongsByPrefix(prefix: String): List<Strong>

    @Query("SELECT * FROM commentaries WHERE verse_id = :verseId ORDER BY type")
    suspend fun getCommentaries(verseId: Long): List<Commentary>

    @Query("SELECT MAX(chapter) FROM verses WHERE book_id = :bookId")
    suspend fun getMaxChapter(bookId: Int): Int?

    @Query("SELECT MAX(verse) FROM verses WHERE book_id = :bookId AND chapter = :chapter")
    suspend fun getMaxVerse(bookId: Int, chapter: Int): Int?

    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE verse_id = :verseId")
    suspend fun getBookmarkForVerse(verseId: Long): Bookmark?

    @Query("DELETE FROM bookmarks WHERE verse_id = :verseId")
    suspend fun removeBookmark(verseId: Long)

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)
}

data class SearchResult(
    val id: Int,
    val name: String,
    val abbreviation: String,
    val testament: Int,
    val book_order: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)
