package app.domain.user

import extensions.messageKey
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName

data class WorkHistory(
    val room: String,
    val requestedManager: User?,
    val approvedManager: User?,
    val requestedAt: Instant,
    val startedAt: Instant,
    val endAt: Instant,
    val approvedAt: Instant,
    val numberOfStar: Int,
    val message: String
)