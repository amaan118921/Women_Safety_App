package com.example.womensafety.core.utils

import com.example.womensafety.core.ResultState
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

fun <T> Response<T>.await(): ResultState<T> {
    return try {
        if(isSuccessful) {
            ResultState.Success(body())
        }else {
            ResultState.Error(null, "Something went wrong...")
        }
    }catch (e: HttpException) {
        ResultState.Error(null, e.localizedMessage)
    }
    catch (e: SocketTimeoutException) {
        ResultState.Error(null, e.localizedMessage)
    }
    catch (e: Exception) {
        ResultState.Error(null, e.localizedMessage)
    }
}