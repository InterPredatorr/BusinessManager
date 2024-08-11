package app.data.work

import extensions.endAtKey
import extensions.managerKey
import extensions.roomNumberKey
import extensions.startedAtKey
import extensions.workerKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkEntity(
    @SerialName(startedAtKey)
    val startedAt: String,
    @SerialName(endAtKey)
    val endAt: String?,
    @SerialName(managerKey)
    val manager: String,
    @SerialName(workerKey)
    val worker: String,
    @SerialName(roomNumberKey)
    val room: String
)