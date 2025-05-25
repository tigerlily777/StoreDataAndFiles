package com.example.storedataandfiles

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Delete
    suspend fun delete(user: User)
}