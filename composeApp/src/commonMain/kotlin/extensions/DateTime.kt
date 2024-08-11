package extensions

import kotlinx.datetime.LocalDateTime

expect class DateTime() {
    fun getFormattedDate(
        iso8601Timestamp: String,
        format: String,
    ): String

    fun getCurrentDateTime(): LocalDateTime

}