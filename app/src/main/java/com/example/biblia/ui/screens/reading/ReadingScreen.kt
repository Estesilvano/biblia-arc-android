package com.example.biblia.ui.screens.reading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblia.data.database.entity.Commentary
import com.example.biblia.data.database.entity.Strong
import com.example.biblia.data.database.entity.Verse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    bookName: String,
    bookAbbr: String,
    chapter: Int,
    maxChapter: Int,
    verses: List<Verse>,
    fontSize: Float,
    lineSpacing: Float,
    selectedVerse: Verse?,
    commentaries: List<Commentary>,
    onBack: () -> Unit,
    onChapterSelect: (Int) -> Unit,
    onVerseClick: (Verse) -> Unit,
    onCloseVerse: () -> Unit,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val showVerseDetail = selectedVerse != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "$bookName $chapter",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "$bookAbbr $chapter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { selectedVerse?.let { onToggleBookmark(it.id) } }) {
                        Icon(
                            if (selectedVerse != null) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Favorito"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Config")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            ChapterNavigation(
                chapter = chapter,
                maxChapter = maxChapter,
                onPrevious = onPreviousChapter,
                onNext = onNextChapter
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(verses) { index, verse ->
                    val isSelected = selectedVerse?.id == verse.id
                    VerseItem(
                        verse = verse,
                        isSelected = isSelected,
                        fontSize = fontSize,
                        lineSpacing = lineSpacing,
                        onClick = { onVerseClick(verse) }
                    )
                }
            }

            if (verses.isEmpty()) {
                Text(
                    "Carregando...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (showVerseDetail) {
        ModalBottomSheet(
            onDismissRequest = onCloseVerse,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            VerseDetailSheet(
                verse = selectedVerse!!,
                commentaries = commentaries,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onCloseVerse() }
                },
                onToggleBookmark = { onToggleBookmark(selectedVerse!!.id) }
            )
        }
    }
}

@Composable
private fun VerseItem(
    verse: Verse,
    isSelected: Boolean,
    fontSize: Float,
    lineSpacing: Float,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else
        MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(bgColor)
            .then(
                if (isSelected) Modifier.padding(vertical = 2.dp)
                else Modifier
            )
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "${verse.verse}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (fontSize * 0.6f).sp,
                    fontFamily = FontFamily.Default
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(28.dp),
                textAlign = TextAlign.End
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = verse.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * lineSpacing).sp,
                    fontFamily = FontFamily.Serif
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        if (isSelected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Detalhes",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Toque para ver comentário e Strong",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChapterNavigation(
    chapter: Int,
    maxChapter: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevious,
            enabled = chapter > 1,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Anterior", fontSize = 13.sp)
        }

        Spacer(Modifier.width(16.dp))

        Text(
            "$chapter / $maxChapter",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.width(16.dp))

        Button(
            onClick = onNext,
            enabled = chapter < maxChapter,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("Próximo", fontSize = 13.sp)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun VerseDetailSheet(
    verse: Verse,
    commentaries: List<Commentary>,
    onClose: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            "Versículo ${verse.verse}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            verse.text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Serif,
                lineHeight = (18 * 1.6f).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        if (commentaries.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text(
                "Comentário Teológico",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            commentaries.forEach { commentary ->
                if (commentary.type == "general") {
                    Text(
                        commentary.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (commentary.type == "theological") {
                    Text(
                        "Profundidade Teológica",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        commentary.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (commentary.type == "context") {
                    Text(
                        "Contexto",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        commentary.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                "Nenhum comentário disponível para este versículo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
