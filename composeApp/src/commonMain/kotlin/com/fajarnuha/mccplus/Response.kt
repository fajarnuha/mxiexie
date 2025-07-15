package com.fajarnuha.mccplus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ApiResponse(
    val success: Boolean,
    @SerialName("errorCode")
    val errorCode: Int,
    val message: String,
    // The 'data' field is a String that contains another JSON object
    val data: String
)

@Serializable
data class AccessDataWrapper(
    val validity: String,
    @SerialName("accessData")
    val accessDataList: List<AccessDataItem>
)

@Serializable
data class AccessDataItem(
    val description: String,
    val token: String
)

fun mockData(): List<AccessDataItem> {
    val jsonString = """
    {
      "success": true,
      "errorCode": 0,
      "message": "",
      "data": "{\"validity\":\"2025-07-14T16:05:55.280521\",\"accessData\":[{\"description\":\"BYTEDANCE 27\",\"token\":\"4219159374\"},{\"description\":\"BYTEDANCE 28\",\"token\":\"4219161374\"},{\"description\":\"BYTEDANCE 30\",\"token\":\"4219160374\"},{\"description\":\"BYTEDANCE 31\",\"token\":\"4219162374\"},{\"description\":\"BYTEDANCE 26\",\"token\":\"4219163374\"},{\"description\":\"BYTEDANCE 29\",\"token\":\"4219164374\"}]}"
    }
    """.trimIndent()

    // 1. Create a Json parser instance
    val json = Json { ignoreUnknownKeys = true }

    // 2. Parse the outer JSON response
    val apiResponse = json.decodeFromString<ApiResponse>(jsonString)

    // 3. Check if the call was successful and the data string is not empty
    if (apiResponse.success && apiResponse.data.isNotEmpty()) {
        // 4. Parse the inner JSON string from the 'data' field
        val accessDataWrapper = json.decodeFromString<AccessDataWrapper>(apiResponse.data)

        // Now you can access the nested data
        println("Validity: ${accessDataWrapper.validity}")
        accessDataWrapper.accessDataList.forEach { item ->
            println("  - Description: ${item.description}, Token: ${item.token}")
        }

        return accessDataWrapper.accessDataList
    } else {
        println("API call failed or data was empty. Message: ${apiResponse.message}")
        return emptyList()
    }
}