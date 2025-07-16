package com.fajarnuha.mccplus

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

expect fun imageBitmapFromBytes(bytes: ByteArray): ImageBitmap

/**
 * A utility object for time-related operations.
 */
object TimeUtils {

    /**
     * Checks if a token's validity timestamp is still in the future.
     *
     * The function parses a timestamp string and compares it against the current system time.
     * The expected format is ISO 8601, e.g., "2025-07-14T16:05:55.280521".
     *
     * @param validityString The timestamp string to check. Can be null or blank.
     * @return `true` if the validity timestamp is after the current time, `false` otherwise.
     *         Also returns `false` if the string is null, blank, or malformed.
     */
    fun isTokenStillValid(validityString: String?): Boolean {
        // A null or blank string is considered invalid.
        if (validityString.isNullOrBlank()) {
            return false
        }

        return try {
            // Parse the string directly into an Instant.
            // Instant.parse() handles the ISO 8601 format automatically.
            val validityInstant = Instant.parse(validityString)

            // Get the current system time as an Instant.
            val now = Clock.System.now()

            // The token is valid if its validity time is after the current time.
            validityInstant > now

        } catch (e: Exception) {
            // This will catch any parsing errors for malformed strings.
            // Log the error for debugging if necessary.
            // println("Error parsing validity string '$validityString': ${e.message}")
            false
        }
    }
}