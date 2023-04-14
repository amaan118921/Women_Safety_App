package com.example.womensafety.domain.usecase

import com.example.womensafety.core.ResultState
import com.example.womensafety.data.room.dao.Dao
import com.example.womensafety.data.room.enitities.AlertEntity
import com.example.womensafety.domain.models.toAlertEntity
import com.example.womensafety.domain.repositories.AppRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertUseCase @Inject constructor(
    private val appRepository: AppRepository,
    private val dao: Dao
) {

    suspend operator fun invoke(uid: String): ResultState<List<AlertEntity>> {
        val localData = dao.getAllAlerts()
        try {
            val remoteData = appRepository.getAlerts(uid).body()?.dataArray
            dao.deleteAllAlerts()
            dao.insertAlertList(remoteData?.map { it.toAlertEntity() } ?: emptyList())
        } catch (e: SocketTimeoutException) {
            return ResultState.Error(localData, e.localizedMessage)
        } catch (e: HttpException) {
            return ResultState.Error(localData, e.localizedMessage)
        } catch (e: IOException) {
            return ResultState.Error(localData, e.localizedMessage)
        } catch (e: Exception) {
            return ResultState.Error(localData, e.localizedMessage)
        }
        val updateData = dao.getAllAlerts()
        return ResultState.Success(updateData)
    }
}