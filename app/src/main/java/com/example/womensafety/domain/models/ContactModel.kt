package com.example.womensafety.domain.models

import com.example.womensafety.data.room.enitities.ContactEntity

data class ContactModel(
    var name: String = "",
    var phone: String = "",
    var userId: String = "",
    var _id: String = "",
    var update: String = ""
)

data class Contacts(
    var dataArray: List<ContactModel>? = null
)


fun ContactModel.toContactEntity(): ContactEntity {
    return ContactEntity(
        name = name,
        phone = phone,
        userId = userId,
        _id = _id
    )
}


fun ContactEntity.toContactModel(): ContactModel {
    return ContactModel(
        name = name,
        phone = phone,
        userId = userId,
        _id = _id
    )
}