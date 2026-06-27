package com.example.biblia.ui.screen.reading

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.biblia.data.entity.Verse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    viewModel: ReadingViewModel,
    onBack: () -> Unit,
    onChapterChange: (Int) -> Unit,
    onHome: () -> Unit
) {
    val book by viewModel.book.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val chapterTitle by viewModel.chapterTitle.collectAsState()
    val commentary by viewModel.commentary.collectAsState()
    val strongDetail by viewModel.strongDetail.collectAsState()

    var showChapterPicker by remember { mutableStateOf(false) }
    var showStrongDialog by remember { mutableStateOf(false) }

    if (showStrongDialog && strongDetail != null) {
        AlertDialog(
            onDismissRequest = { showStrongDialog = false; viewModel.dismissStrong() },
            title = { Text("Strong ${strongDetail!!.number}") },
            text = {
                Column {
                    strongDetail!!.word?.let { Text("Palavra: $it", style = MaterialTheme.typography.bodyLarge) }
                    strongDetail!!.transliteration?.let { Text("Transliteração: $it", style = MaterialTheme.typography.bodyMedium) }
                    Spacer(Modifier.height(8.dp))
                    Text("${strongDetail!!.definition}")
                    Spacer(Modifier.height(4.dp))
                    Text("Idioma: ${if (strongDetail!!.language == "hebrew") "Hebraico" else "Grego"}",
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showStrongDialog = false; viewModel.dismissStrong() }) { Text("Fechar") }
            }
        )
    }

    if (showChapterPicker && chapters.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showChapterPicker = false },
            title = { Text("Selecionar Capítulo") },
            text = {
                LazyColumn {
                    itemsIndexed(chapters) { _, ch ->
                        TextButton(
                            onClick = {
                                showChapterPicker = false
                                viewModel.changeChapter(ch)
                                onChapterChange(ch)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Capítulo $ch", fontWeight = if (ch == viewModel.chapter) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chapterTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                },
                actions = {
                    IconButton(onClick = onHome) { Icon(Icons.Default.Home, "Início") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentChapter = viewModel.chapter,
                chapters = chapters,
                onChapterSelect = { ch ->
                    viewModel.changeChapter(ch)
                    onChapterChange(ch)
                },
                onShowPicker = { showChapterPicker = true }
            )
        }
    ) { padding ->
        if (verses.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                itemsIndexed(verses) { _, verse ->
                    VerseItem(
                        verse = verse,
                        isBookmarked = verse.id in bookmarkedIds,
                        hasCommentary = false,
                        onBookmarkToggle = { viewModel.toggleBookmark(verse.id) },
                        onCommentaryClick = {
                            viewModel.loadCommentary(verse.id)
                        },
                        onStrongClick = { num ->
                            viewModel.selectStrong(num)
                            showStrongDialog = true
                        }
                    )
                }
            }
        }
    }

    if (commentary != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissCommentary() },
            title = { Text(commentary!!.title ?: "Comentário") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text(commentary!!.text)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissCommentary() }) { Text("Fechar") }
            }
        )
    }
}

@Composable
private fun VerseItem(
    verse: Verse,
    isBookmarked: Boolean,
    hasCommentary: Boolean,
    onBookmarkToggle: () -> Unit,
    onCommentaryClick: () -> Unit,
    onStrongClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${verse.verse}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp, top = 2.dp).width(24.dp)
                )
                Text(
                    text = verse.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 32.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        "Marcador",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onCommentaryClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Info,
                        "Comentário",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentChapter: Int,
    chapters: List<Int>,
    onChapterSelect: (Int) -> Unit,
    onShowPicker: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val prevChapter = currentChapter - 1
            val nextChapter = currentChapter + 1
            val hasPrev = prevChapter in chapters
            val hasNext = nextChapter in chapters

            IconButton(
                onClick = { onChapterSelect(prevChapter) },
                enabled = hasPrev
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Anterior")
            }

            TextButton(onClick = onShowPicker) {
                Text("Capítulo $currentChapter", fontWeight = FontWeight.Medium)
            }

            IconButton(
                onClick = { onChapterSelect(nextChapter) },
                enabled = hasNext
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Próximo")
            }
        }
    }
}
