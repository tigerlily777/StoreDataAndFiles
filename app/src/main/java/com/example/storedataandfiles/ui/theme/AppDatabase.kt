package com.example.storedataandfiles.ui.theme

import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase

abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}

