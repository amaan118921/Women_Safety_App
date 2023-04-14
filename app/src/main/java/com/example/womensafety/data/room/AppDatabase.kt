package com.example.womensafety.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.womensafety.data.room.dao.Dao
import com.example.womensafety.data.room.enitities.AlertEntity
import com.example.womensafety.data.room.enitities.ContactEntity

@Database(entities = [ContactEntity::class, AlertEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): Dao
}