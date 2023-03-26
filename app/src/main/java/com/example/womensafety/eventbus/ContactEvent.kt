package com.example.womensafety.eventbus

import com.example.womensafety.domain.models.ContactModel

class ContactEvent(msg: String, val list: List<ContactModel>): MessageEvent(msg) {
    fun getContactsList(): List<ContactModel> =  list
}