package app.data.user

import app.business.manager.database.UserDatabase
import app.data.cache.CacheService
import app.data.local.toUsersList

class UserCacheService(db: UserDatabase) : CacheService<UserEntity> {
    private val queries = db.usersQueries

    private val users = mutableListOf<UserEntity>()
    override suspend fun insert(data: UserEntity) {
        users.add(data)
        /* queries.insert(
             id = null,
             uid = data.id,
             name = data.name,
             age = data.age.toLong(),
             password = data.password,
             role = data.role,
             address = data.address,
             phone = data.phone,
             active = if (data.active) 1L else 0L,
             avatar = data.avatar
         )*/
    }

    override suspend fun insert(data: List<UserEntity>) {
        data.forEach { user ->
            insert(user)
        }
    }

    override suspend fun getAll(): List<UserEntity> {
//        queries.getAll()
//        return queries.getAll().executeAsList().toUsersList()
        return users
    }

    override suspend fun deleteAll() {
        /* queries.transaction {
             queries.deleteAll()
         }*/
        users.clear()
    }

    override suspend fun deleteIf(action: (UserEntity) -> Boolean) {
        val users = queries.getAll().executeAsList().toUsersList()
        val usersToDelete = users.filter(action)

        usersToDelete.forEach { user ->
            queries.deleteByUid(user.id)
        }
    }

    override suspend fun getIf(action: (UserEntity) -> Boolean): List<UserEntity> {
//        val users = queries.getAll().executeAsList().toUsersList()
        return users.filter(action)
    }

    override suspend fun updateUserData(id: String, data: Map<String, Any>) {
        val user = getIf { it.id==id }.firstOrNull() ?: return
        data.forEach {
            if (it.key=="avatar") {
                user.avatar = it.value as? String
            }
        }
    }
}
