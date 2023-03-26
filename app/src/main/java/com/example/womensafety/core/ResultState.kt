package com.example.womensafety.core

sealed class ResultState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): ResultState<T>(data)
    class Loading<T>(data: T?): ResultState<T>(data)
    class Error<T>(data: T?, msg: String): ResultState<T>(data, msg)
}
