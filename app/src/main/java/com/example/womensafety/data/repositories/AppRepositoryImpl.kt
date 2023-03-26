package com.example.womensafety.data.repositories

import com.example.womensafety.domain.repositories.AppRepository
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.Contacts
import com.example.womensafety.domain.models.DataResponse
import com.example.womensafety.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(private val apiService: ApiService): AppRepository {

    override suspend fun getAlertContacts(userId: String): Response<Contacts> {
        return apiService.getAlertContacts(userId)
    }

    override suspend fun addAlertContacts(contactModel: ContactModel): Response<ContactModel> {
        return apiService.addAlertContacts(contactModel)
    }

    override suspend fun deleteContact(_id: String, userId: String): Response<ContactModel> {
        return apiService.deleteContact(_id, userId)
    }

}