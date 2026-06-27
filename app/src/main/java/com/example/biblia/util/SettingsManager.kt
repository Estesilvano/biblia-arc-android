package com.example.biblia.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString("theme", "dark") ?: "dark")

    var themeMode: String
        get() = _themeMode.value
        set(value) {
            prefs.edit().putString("theme", value).apply()
            _themeMode.value = value
        }

    var fontSize: Int
        get() = prefs.getInt("fontSize", 20)
        set(value) = prefs.edit().putInt("fontSize", value).apply()

    var lineSpacing: Int
        get() = prefs.getInt("lineSpacing", 8)
        set(value) = prefs.edit().putInt("lineSpacing", value).apply()

    fun observeTheme(): StateFlow<String> = _themeMode.asStateFlow()
}
