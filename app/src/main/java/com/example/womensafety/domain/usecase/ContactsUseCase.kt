package com.example.womensafety.domain.usecase

import com.example.womensafety.core.ResultState
import com.example.womensafety.data.room.dao.Dao
import com.example.womensafety.data.room.enitities.ContactEntity
import com.example.womensafety.domain.models.toContactEntity
import com.example.womensafety.domain.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton


class ContactsUseCase @Inject constructor(
    private val appRepository: AppRepository,
    private val dao: Dao
) {

    suspend operator fun invoke(uid: String): ResultState<List<ContactEntity>> {
        val localData = dao.getAllContacts()
        try {
            val remoteData = appRepository.getAlertContacts(uid).body()?.dataArray
            dao.deleteAll()
            dao.insertList(remoteData?.map { it.toContactEntity() }?: emptyList())
        } catch (e: SocketTimeoutException) {
            return ResultState.Error(localData, e.localizedMessage)
        } catch (e: HttpException) {
            return ResultState.Error(localData, e.localizedMessage)
        } catch (e: IOException) {
            return ResultState.Error(localData, e.localizedMessage)
        } catch (e: Exception) {
            return ResultState.Error(localData, e.localizedMessage)
        }
        val updateData = dao.getAllContacts()
        return ResultState.Success(updateData)
    }
}