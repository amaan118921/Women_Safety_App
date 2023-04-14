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
class DeleteContactsUseCase @Inject constructor(private val appRepository: AppRepository) {

    operator fun invoke(_id: String, uid: String): Flow<ResultState<ContactModel>> = flow {
        try {
            emit(ResultState.Loading(null))
            val response = appRepository.deleteContact(_id, uid).body()
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