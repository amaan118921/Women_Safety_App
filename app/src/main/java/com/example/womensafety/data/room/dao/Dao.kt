package com.example.womensafety.data.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.womensafety.data.room.enitities.ContactEntity

@androidx.room.Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(contactList: List<ContactEntity>)

    @Query("SELECT * FROM ContactEntity")
    fun getAllContacts(): List<ContactEntity>

    @Delete
    suspend fun deleteList(contactList: List<ContactEntity>)

    @Query("DELETE FROM ContactEntity")
    suspend fun deleteAll()
}