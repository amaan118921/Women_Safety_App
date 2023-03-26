package com.example.womensafety.data.network

import com.example.womensafety.helpers.Constants
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.Contacts
import com.example.womensafety.domain.models.DataResponse
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(Constants.BASE_URL).build()
private val postRetrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(Constants.BASE_URL).build()
interface ApiService {

    @GET("/contacts/{userId}")
    suspend fun getAlertContacts(@Path("userId") userId: String): Response<Contacts>

    @POST("/contacts")
    suspend fun addAlertContacts(@Body contactModel: ContactModel): Response<ContactModel>

    @DELETE("/contacts/{id}/{userId}")
    suspend fun deleteContact(@Path("id") _id: String, @Path("userId") userId: String): Response<ContactModel>
}