package app.data.user

import extensions.approvedAtKey
import extensions.approvedManagerKey
import extensions.endAtKey
import extensions.messageKey
import extensions.numberOfStarsKey
import extensions.requestedAtKey
import extensions.requestedManagerKey
import extensions.roomNumberKey
import extensions.startedAtKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserHistoryEntity(
    @SerialName(roomNumberKey)
    val room: String,
    @SerialName(requestedManagerKey)
    val requestedManager: String,
    @SerialName(approvedManagerKey)
    val approvedManager: String,
    @SerialName(requestedAtKey)
    val requestedAt: String,
    @SerialName(startedAtKey)
    val startAt: String,
    @SerialName(endAtKey)
    val endAt: String,
    @SerialName(approvedAtKey)
    val approvedAt: String,
    @SerialName(numberOfStarsKey)
    val numberOfStar: Int? = null,
    @SerialName(messageKey)
    val message: String? = null
)