package com.example.womensafety.domain.models

import com.example.womensafety.data.room.enitities.AlertEntity

data class AlertModel(
    var name: String? = null,
    var phone: String? = null,
    var userId: String? = null,
    var timeStamp: String? = null,
    var _id: String? = null,
    var update: String? = null
)

fun AlertModel.toAlertEntity(): AlertEntity {
    return AlertEntity(
        name = name?:"",
        phone = phone?:"",
        userId = userId?:"",
        timeStamp = timeStamp?:"",
        _id = _id?:""
    )
}