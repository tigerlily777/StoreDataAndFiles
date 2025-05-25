package com.example.storedataandfiles

import androidx.room.RoomDatabase

abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}