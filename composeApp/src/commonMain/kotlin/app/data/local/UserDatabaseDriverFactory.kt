package app.data.local

import app.cash.sqldelight.db.SqlDriver
import app.data.user.UserEntity
import app.domain.user.User
import database.User_entity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class UserDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun List<UserEntity>.toJson(): String {
    return Json.encodeToString(this)
}

fun List<User_entity>.toUsersList(): List<UserEntity> {
    return this.map { user ->
        UserEntity(
            id = user.uid,
            name = user.name,
            password = user.password,
            age = user.age.toInt(),
            role = user.role,
            active = user.active==1L,
            avatar = user.avatar,
            address = user.address,
            phone = user.phone,
            startingDate = user.startingDate
        )
    }
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        password = null,
        age = this.age,
        role = this.role.getName(),
        active = active,
        address = address,
        avatar = imageUrl,
        phone = phoneNumber,
        startingDate = startingDate
    )
}