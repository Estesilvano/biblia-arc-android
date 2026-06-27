package com.example.biblia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.biblia.data.repository.BibleRepository
import com.example.biblia.ui.navigation.AppNavigation
import com.example.biblia.ui.theme.BibliaTheme
import com.example.biblia.util.SettingsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as BibliaApp
        val repository = BibleRepository(app.db.bibleDao())
        val settingsManager = SettingsManager(this)

        setContent {
            val themeMode by settingsManager.observeTheme()
            BibliaTheme(themeMode = themeMode) {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    repository = repository,
                    settingsManager = settingsManager
                )
            }
        }
    }
}
