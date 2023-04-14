package com.example.womensafety.data.room.enitities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_table")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var phone: String,
    @ColumnInfo
    var userId: String,
    @ColumnInfo
    var _id: String,
    @ColumnInfo
    var timeStamp: String
)
