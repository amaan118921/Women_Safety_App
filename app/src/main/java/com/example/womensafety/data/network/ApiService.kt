package com.example.womensafety.data.network

import com.example.womensafety.domain.models.Alerts
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.Contacts
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/contacts/{userId}")
    suspend fun getAlertContacts(@Path("userId") userId: String): Response<Contacts>

    @GET("/alerts/{userId}")
    suspend fun getAlerts(@Path("userId") userId: String): Response<Alerts>

    @POST("/contacts")
    suspend fun addAlertContacts(@Body contactModel: ContactModel): Response<ContactModel>

    @DELETE("/contacts/{id}/{userId}")
    suspend fun deleteContact(
        @Path("id") _id: String,
        @Path("userId") userId: String
    ): Response<ContactModel>
}