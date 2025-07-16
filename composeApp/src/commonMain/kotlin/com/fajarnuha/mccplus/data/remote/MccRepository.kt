package com.fajarnuha.mccplus.data.remote

import com.fajarnuha.mccplus.AccessDataWrapper
import com.fajarnuha.mccplus.TimeUtils
import com.fajarnuha.mccplus.data.local.SettingsRepository
import com.fajarnuha.mccplus.data.local.createDataStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

// Response data class for the Login endpoint
@Serializable
data class LoginResponse(
    val success: Boolean,
    val token: String? = null, // Nullable for failure cases
    val description: String? = null,
    val tokenValidity: String? = null,
    val message: String? = null // For API error messages
)

// Response data classes for the Access endpoint
@Serializable
data class AccessResponse(
    val success: Boolean,
    val errorCode: Int? = null,
    val message: String? = null,
    val data: String? = null // Assuming data is a JSON string
)

class ApiClient(private val settings: SettingsRepository = SettingsRepository(createDataStore())) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Good practice for API changes
            })
        }
    }

    /**
     * Performs a login request.
     * @return A Result containing a LoginResponse on success, or an Exception on failure.
     */
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            // Make the POST request and expect a LoginResponse object
            val response: LoginResponse =
                client.post("https://cc.project-on.net:30010/api/cc/v1/Login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(username, password))
                }.body()

            // Check the success flag from the response body
            if (response.success) {
                response.token?.let { settings.setToken(it) }
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Login failed with an unknown error"))
            }
        } catch (e: Exception) {
            // Handle network errors, serialization errors, etc.
            Result.failure(e)
        }
    }

    /**
     * Fetches access data using a bearer token.
     * @return A Result containing AccessData on success, or an Exception on failure.
     */
    suspend fun getAccessData(force: Boolean = false): Result<AccessDataWrapper> {
        return try {

            val cache = runCatching { Json.decodeFromString<AccessDataWrapper>(settings.getAccessCache()!!) }.getOrNull()
            if (cache != null && TimeUtils.isTokenStillValid(cache.validity) && !force) {
                return Result.success(cache)
            }

            val response: AccessResponse = client.post("https://cc.project-on.net:30010/api/cc/v1/Access") {
                header("Authorization", "Bearer ${settings.getToken()}")
                contentType(ContentType.Application.Json)
            }.body()

            if (response.success && !response.data.isNullOrBlank()) {
                // Decode the inner JSON string from the 'data' field
                val model = Json.decodeFromString<AccessDataWrapper>(response.data)

                settings.setAccessCache(response.data)
                Result.success(model)
            } else {
                settings.setToken(null)
                Result.failure(Exception(response.message ?: "Failed to get access data."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
