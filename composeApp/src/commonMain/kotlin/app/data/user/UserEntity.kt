package app.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserEntity(
    @SerialName(idKey)
    val id: String,
    @SerialName(nameKey)
    val name: String,
    @SerialName(passwordKey)
    val password: String?,
    @SerialName(ageKey)
    val age: Int,
    @SerialName(roleKey)
    val role: String,
    @SerialName(activeKey)
    val active: Boolean,
    @SerialName(imageUrlKey)
    var avatar: String?,
    @SerialName(phoneNumberKey)
    var phone: String?,
    @SerialName(addressKey)
    var address: String?,
    @SerialName(startingDateKey)
    var startingDate: String
)

const val idKey = "id"
const val nameKey = "name"
const val emailKey = "email"
const val passwordKey = "password"
const val ageKey = "age"
const val roleKey = "role"
const val activeKey = "active"
const val imageUrlKey = "avatar"
const val phoneNumberKey = "phone"
const val addressKey = "address"
const val startingDateKey = "startingDate"
