package com.example.biblia

import android.app.Application
import com.example.biblia.data.database.AppDatabase

class BibliaApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
