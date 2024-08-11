package app.domain.user

import app.data.cache.CacheService
import app.data.local.toUserEntity
import app.data.network.NetworkResult
import app.data.user.GetUserApi
import app.data.user.UserEntity
import dev.gitlive.firebase.storage.File

interface UserProviderUseCase {
    suspend fun getCurrentUser(): User?

    suspend fun fetchUsers(): List<User>

    suspend fun getUser(id: String): User

    suspend fun getUsers(): List<User>

    suspend fun getUsers(type: UserType): List<User>

    suspend fun addUser(user: User)

    suspend fun changeUserStatus(status: Boolean): NetworkResult<Boolean>

    suspend fun addImageToFirebaseStorage(image: File, userId: String): NetworkResult<String>

    suspend fun updateUserData(key: String, value: Map<String, Any>): NetworkResult<Boolean>

}

class UserProviderUseCaseImpl(private val cacheService: CacheService<UserEntity>, private val getUserApi: GetUserApi) : UserProviderUseCase {
    override suspend fun getCurrentUser(): User? {
        return when (val user = getUserApi.getCurrentUser()) {
            is NetworkResult.Success -> {
                return userEntityToUserMapper.map(user.data)
            }

            is NetworkResult.Error -> {
                null
            }
        }
    }

    override suspend fun fetchUsers(): List<User> {
        return when (val usersResult = getUserApi.getUsers()) {
            is NetworkResult.Success -> {
                val usersEntity = usersResult.data
                cacheService.deleteAll()
                cacheService.insert(usersEntity)

                userEntityToUserMapper.map(usersEntity)
            }

            is NetworkResult.Error -> {
                //ToDo handle error case
                emptyList<User.Worker>()
            }
        }
    }

    override suspend fun getUser(id: String): User {
        val userEntity = cacheService.getIf { user ->
            user.id==id
        }.firstOrNull() ?: return User.Unhandled
        return userEntityToUserMapper.map(userEntity)
    }

    override suspend fun getUsers(): List<User> {
        val usersEntity = cacheService.getAll()

        return userEntityToUserMapper.map(usersEntity)
    }

    override suspend fun getUsers(type: UserType): List<User> {
        val userEntity = cacheService.getIf { user ->
            user.role==type.getName()
        }
        return userEntityToUserMapper.map(userEntity)
    }

    override suspend fun addUser(user: User) {
        cacheService.insert(user.toUserEntity())
    }

    override suspend fun changeUserStatus(status: Boolean): NetworkResult<Boolean> {
        return getUserApi.changeUserStatus(status)
    }

    override suspend fun addImageToFirebaseStorage(image: File, userId: String): NetworkResult<String> {
        val result = getUserApi.addImageToFirebaseStorage(image, userId)

        when (result) {
            is NetworkResult.Success -> {
                cacheService.updateUserData(userId, mapOf("avatar" to result.data))
            }

            is NetworkResult.Error -> {

            }
        }

        return result
    }

    override suspend fun updateUserData(key: String, value: Map<String, Any>): NetworkResult<Boolean> {
        return getUserApi.updateUserData(key, value)
    }
}