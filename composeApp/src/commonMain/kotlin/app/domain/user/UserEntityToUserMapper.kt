package app.domain.user

import app.data.user.UserEntity
import extensions.Mapper

val userEntityToUserMapper = object : Mapper<UserEntity, User> {
    override fun map(s: UserEntity): User {
        return when (s.role.toUserType()) {
            UserType.Worker -> User.Worker(
                id = s.id,
                name = s.name,
                status = s.active,
                imageUrl = s.avatar ?: "",
                address = s.address ?: "",
                phoneNumber = s.phone ?: "",
                age = s.age,
                startingDate = s.startingDate)

            UserType.Manager -> User.Manager(
                id = s.id,
                name = s.name,
                age = s.age,
                status = s.active,
                imageUrl = s.avatar ?: "",
                address = s.address ?: "",
                phoneNumber = s.phone ?: "",
                startingDate = s.startingDate)

            UserType.Owner -> User.Owner(
                id = s.id,
                name = s.name,
                age = s.age,
                status = s.active,
                imageUrl = s.avatar ?: "",
                address = s.address ?: "",
                phoneNumber = s.phone ?: "",
                startingDate = s.startingDate)

            else -> User.Unhandled
        }
    }
}

val userToUserEntityMapper = object : Mapper<User, UserEntity> {
    override fun map(s: User): UserEntity {
        return UserEntity(
            id = s.id, name = s.name,
            password = null, age = s.age,
            role = s.role.getName(), active = s.active,
            avatar = s.imageUrl, phone = s.phoneNumber,
            address = s.address, startingDate = s.startingDate)
    }
}

