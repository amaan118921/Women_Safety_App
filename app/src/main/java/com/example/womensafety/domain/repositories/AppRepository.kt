package com.example.womensafety.domain.repositories

import com.example.womensafety.domain.models.AlertModel
import com.example.womensafety.domain.models.Alerts
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.Contacts
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface AppRepository {

    suspend fun getAlertContacts(userId: String): Response<Contacts>

    suspend fun getAlerts(userId: String): Response<Alerts>

    suspend fun addAlertContacts(contactModel: ContactModel): Response<ContactModel>

    suspend fun deleteContact(_id: String, userId: String): Response<ContactModel>

}