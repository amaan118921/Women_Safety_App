package com.example.womensafety.domain.usecase

import com.example.womensafety.core.ResultState
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddContactUseCase @Inject constructor(private val appRepository: AppRepository) {

    suspend operator fun invoke(contactModel: ContactModel): Flow<ResultState<ContactModel>> =
        flow {
            emit(ResultState.Loading(null))
            try {
                val response = appRepository.addAlertContacts(contactModel).body()
                emit(ResultState.Success(response))
            } catch (e: SocketTimeoutException) {
                emit(ResultState.Error(null, e.localizedMessage))
            } catch (e: HttpException) {
                emit(ResultState.Error(null, e.localizedMessage))
            } catch (e: IOException) {
                emit(ResultState.Error(null, e.localizedMessage))
            } catch (e: Exception) {
                emit(ResultState.Error(null, e.localizedMessage))
            }
        }
}