package app.domain.user

import extensions.Timer
import extensions.toYearAndMonth

enum class UserType {
    Worker,
    Manager,
    Owner;

    fun getName(): String {
        return when (this) {
            Worker -> "worker"
            Manager -> "manager"
            Owner -> "owner"
        }
    }

    fun getArmName(): String {
        return when (this) {
            Worker -> "Աշխատող"
            Manager -> "Մենեջեր"
            Owner -> "Տնօրեն"
        }
    }
}

fun String.toUserType(): UserType? {
    return when (this) {
        "worker" -> UserType.Worker
        "manager" -> UserType.Manager
        "owner" -> UserType.Owner
        else -> null
    }
}

fun String.armToUserType(): UserType? {
    return when (this) {
        "Աշխատող" -> UserType.Worker
        "Մենեջեր" -> UserType.Manager
        "Տնօրեն" -> UserType.Owner
        else -> null
    }
}

sealed class User(val id: String, val name: String, val age: Int, val active: Boolean, val imageUrl: String, val address: String, val phoneNumber: String, val role: UserType, val startingDate: String) {
    class Manager(id: String, name: String, age: Int, status: Boolean, imageUrl: String, address: String, phoneNumber: String, startingDate: String) : User(id, name, age, status, imageUrl, address, phoneNumber, UserType.Manager, startingDate)
    class Owner(id: String, name: String, age: Int, status: Boolean, imageUrl: String, address: String, phoneNumber: String, startingDate: String) : User(id, name, age, status, imageUrl, address, phoneNumber, UserType.Owner, startingDate)
    class Worker(id: String, name: String, status: Boolean, imageUrl: String, address: String, phoneNumber: String, age: Int, startingDate: String) : User(id, name, age, status, imageUrl, address, phoneNumber, UserType.Worker, startingDate)

    object Unhandled : User("", "", 1, false, "", "", "", UserType.Manager, "")
}