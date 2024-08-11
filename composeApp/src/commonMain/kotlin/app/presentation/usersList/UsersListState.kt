package app.presentation.usersList

import app.domain.user.User

data class UsersListState(
    val users: List<User> = emptyList()
) {
}