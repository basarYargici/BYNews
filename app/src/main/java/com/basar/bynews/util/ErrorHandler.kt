package com.basar.bynews.util

import com.basar.bynews.util.HttpExceptionType.BadRequest
import com.basar.bynews.util.HttpExceptionType.CustomError
import com.basar.bynews.util.HttpExceptionType.Forbidden
import com.basar.bynews.util.HttpExceptionType.GatewayTimeout
import com.basar.bynews.util.HttpExceptionType.InternalServerError
import com.basar.bynews.util.HttpExceptionType.NoConnection
import com.basar.bynews.util.HttpExceptionType.NoContent
import com.basar.bynews.util.HttpExceptionType.NotFound
import com.basar.bynews.util.HttpExceptionType.RequestTimeout
import com.basar.bynews.util.HttpExceptionType.Unauthorized
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

object ErrorHandler {
    private const val CONNECTION_LOST = "Connection Lost"
    private const val TIMEOUT = "Time Out"
    private const val GENERAL_ERROR = "Error Occurred"
    private const val BAD_REQUEST = "Bad Request"
    private const val UNAUTHORIZED = "Unauthorized"
    private const val FORBIDDEN = "Forbidden"
    private const val NOT_FOUND = "Not Found"
    private const val NO_CONTENT = "No Content"
    private const val GATEWAY_TIMEOUT = "Gateway Timeout"
    private const val INTERNAL_SERVER_ERROR = "Internal Server Error"

    fun handle(
        t: Throwable,
        callBack: ((HttpExceptionType) -> Unit)? = null,
    ): HttpExceptionType {
        val error = when (t) {
            is SocketTimeoutException -> RequestTimeout(TIMEOUT)
            is EOFException -> NoContent(NO_CONTENT)
            is UnknownHostException, is ConnectException, is UnknownServiceException, is IOException -> NoConnection(
                CONNECTION_LOST
            )

            is HttpException -> handleHttpException(t)
            else -> InternalServerError(t.localizedMessage ?: GENERAL_ERROR)
        }
        callBack?.invoke(error)
        return error
    }

    private fun handleHttpException(t: HttpException): HttpExceptionType {
        return when (t.code()) {
            504 -> GatewayTimeout(GATEWAY_TIMEOUT)
            400 -> BadRequest(BAD_REQUEST)
            401 -> Unauthorized(UNAUTHORIZED)
            403 -> Forbidden(FORBIDDEN)
            404 -> NotFound(NOT_FOUND)
            500 -> InternalServerError(INTERNAL_SERVER_ERROR)
            else -> try {
                val result = t.response()?.errorBody()?.string()
                val json = Gson().fromJson(result, Any::class.java)
                val message = json.toString()

                CustomError(message, t.code())
            } catch (e: Throwable) {
                InternalServerError(e.localizedMessage ?: INTERNAL_SERVER_ERROR)
            }
        }
    }
}

sealed class HttpExceptionType(val message: String, val code: Int = 0) {
    class NoConnection(message: String) : HttpExceptionType(message, 0)
    class BadRequest(message: String) : HttpExceptionType(message, 400)
    class Unauthorized(message: String) : HttpExceptionType(message, 401)
    class Forbidden(message: String) : HttpExceptionType(message, 403)
    class NotFound(message: String) : HttpExceptionType(message, 404)
    class RequestTimeout(message: String) : HttpExceptionType(message, 408)
    class NoContent(message: String) : HttpExceptionType(message, 204)
    class GatewayTimeout(message: String) : HttpExceptionType(message, 504)
    class InternalServerError(message: String) : HttpExceptionType(message, 500)
    class CustomError(message: String, code: Int) : HttpExceptionType(message, code)
}
