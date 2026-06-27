package com.example.biblia.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.biblia.util.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit
) {
    var themeMode by remember { mutableStateOf(settingsManager.themeMode) }
    var fontSize by remember { mutableStateOf(settingsManager.fontSize) }
    var lineSpacing by remember { mutableStateOf(settingsManager.lineSpacing) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text("Tema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("dark" to "Escuro", "sepia" to "Sépia", "light" to "Claro", "system" to "Sistema").forEach { (value, label) ->
                    FilterChip(
                        selected = themeMode == value,
                        onClick = {
                            themeMode = value
                            settingsManager.themeMode = value
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text("Tamanho da Fonte", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("${fontSize}sp", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = fontSize.toFloat(),
                onValueChange = { fontSize = it.toInt() },
                onValueChangeFinished = { settingsManager.fontSize = fontSize },
                valueRange = 14f..32f,
                steps = 17
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text("Espaçamento entre Linhas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("${lineSpacing}dp", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = lineSpacing.toFloat(),
                onValueChange = { lineSpacing = it.toInt() },
                onValueChangeFinished = { settingsManager.lineSpacing = lineSpacing },
                valueRange = 2f..20f,
                steps = 17
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text("Sobre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Bíblia ARC (Almeida Revista e Corrigida)", style = MaterialTheme.typography.bodyMedium)
            Text("Versão 1.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
