package com.example.biblia

import android.app.Application
import com.example.biblia.data.AppDatabase

class BibliaApp : Application() {
    val db: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
