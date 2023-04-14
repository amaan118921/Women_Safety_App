package com.example.womensafety.di

import android.app.Application
import androidx.room.Room
import com.example.womensafety.data.network.ApiService
import com.example.womensafety.data.repositories.AppRepositoryImpl
import com.example.womensafety.data.room.AppDatabase
import com.example.womensafety.data.room.dao.Dao
import com.example.womensafety.domain.repositories.AppRepository
import com.example.womensafety.helpers.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(
            Constants.BASE_URL).build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOKClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideAppRepository(apiService: ApiService): AppRepository {
        return AppRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideAppDao(appDatabase: AppDatabase): Dao {
        return appDatabase.getDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        var INSTANCE: AppDatabase? = null
        return INSTANCE?: synchronized(this) {
            val instance = Room.databaseBuilder(application.applicationContext, AppDatabase::class.java, "contact_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
            INSTANCE = instance
            return instance
        }
    }

}