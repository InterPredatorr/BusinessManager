package app.domain.login

import app.data.login.LoginApi
import app.data.network.NetworkResult
import app.data.user.GetUserApi
import app.data.user.UserEntity
import app.data.user.ageKey
import app.data.user.nameKey
import app.data.user.roleKey
import app.data.user.startingDateKey
import app.domain.user.User
import app.domain.user.UserType
import app.domain.user.userEntityToUserMapper
import extensions.DateTime
import extensions.Timer
import extensions.toDate
import kotlinx.datetime.Clock

interface LoginUseCase {
    suspend fun register(name: String, password: String, age: Int, userType: String): NetworkResult<User>

    suspend fun login(name: String, password: String): NetworkResult<User>

    suspend fun logOut()

    suspend fun createNewWorkerAccount(name: String, password: String, age: Int, userType: UserType): NetworkResult<User>

}

class LoginUseCaseImpl(val loginApi: LoginApi, val getUserApi: GetUserApi) : LoginUseCase {
    override suspend fun register(name: String, password: String, age: Int, userType: String): NetworkResult<User> {
        val userId = when (val result = loginApi.register(name, password)) {
            is NetworkResult.Success -> {
                result.data
            }

            is NetworkResult.Error -> {
                null
            }
        }
        return userId?.let { uid ->
            val userData = HashMap<String, Any>()
            userData[nameKey] = name
            userData[ageKey] = age
            userData[roleKey] = userType
            userData[startingDateKey] = Clock.System.now().toDate()
            userData["password"] = password
            try {
                getUserApi.setUserData(uid, userData)
                val userEntity = UserEntity(id = uid, name = name, password = password, age = age, role = userType, active = false, avatar = null, phone = null, address = null, startingDate = Timer.Now.toString())
                NetworkResult.Success(userEntityToUserMapper.map(userEntity))
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        } ?: NetworkResult.Error(Exception("Can not login"))
    }

    override suspend fun login(name: String, password: String): NetworkResult<User> {
        val result = loginApi.signIn(name, password)
        val userId = when (result) {
            is NetworkResult.Success -> {
                result.data
            }

            is NetworkResult.Error -> {
                null
            }
        }
        return userId?.let { uid ->
            val userResult = getUserApi.getUser(uid)
            when (userResult) {
                is NetworkResult.Success -> {
                    NetworkResult.Success(userEntityToUserMapper.map(userResult.data))
                }

                is NetworkResult.Error -> {
                    NetworkResult.Error(Exception("Can not login"))
                }

            }
        } ?: NetworkResult.Error(Exception("Can not login"))
    }

    override suspend fun logOut() {
        loginApi.signOut()
    }

    override suspend fun createNewWorkerAccount(name: String, password: String, age: Int, userType: UserType): NetworkResult<User> {
        val currentUser = (getUserApi.getCurrentUser() as? NetworkResult.Success<UserEntity>)?.data
            ?: return NetworkResult.Error(Exception("There is no current user"))
        val currentUserPassword = currentUser.password ?: return NetworkResult.Error(Exception("There is no current user"))
        return when (val registerResult = register(name, password, age, userType.getName())) {
            is NetworkResult.Success -> {
                loginApi.signOut()
                login(currentUser.name, currentUserPassword)
            }

            is NetworkResult.Error -> {
                NetworkResult.Error(registerResult.exception)
            }
        }
    }
}