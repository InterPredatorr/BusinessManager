@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package app.data.user

import app.data.network.FirebaseBaseNetworkApi
import app.data.network.NetworkResult
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.storage.File

interface GetUserApi {
    suspend fun getCurrentUser(): NetworkResult<UserEntity>

    suspend fun getUsers(): NetworkResult<List<UserEntity>>

    suspend fun getUser(id: String): NetworkResult<UserEntity>

    suspend fun updateCurrentUserData(data: Map<String, Any>): NetworkResult<Boolean>

    suspend fun updateUserData(id: String, data: Map<String, Any>): NetworkResult<Boolean>

    suspend fun setUserData(id: String, data: Map<String, Any>): NetworkResult<Boolean>

    suspend fun changeUserStatus(status: Boolean): NetworkResult<Boolean>

    suspend fun addImageToFirebaseStorage(image: File, userId: String): NetworkResult<String>
}

class GetUserFirebaseApi : GetUserApi, FirebaseBaseNetworkApi() {
    override suspend fun getCurrentUser(): NetworkResult<UserEntity> {
        val firebaseCurrentUser =
            auth.currentUser ?: return NetworkResult.Error(Exception("There is no user"))

        return getUser(firebaseCurrentUser.uid)
    }

    override suspend fun getUser(id: String): NetworkResult<UserEntity> {
        return try {
            val doc = usersFireStore.document(id).get()

            NetworkResult.Success(docToUser(doc))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    private fun docToUser(doc: DocumentSnapshot): UserEntity {
        val id = doc.id
        val age = doc.get<Int>(ageKey)
        val name = doc.get<String>(nameKey)
        val role = doc.get<String>(roleKey)
        val password = doc.get<String?>(passwordKey)
        val address = doc.get<String?>(addressKey)
        val imageUrl = doc.get<String?>(imageUrlKey)
        val status = doc.get<Boolean?>(activeKey)
        val phoneNumber = doc.get<String?>(phoneNumberKey)
        val startingDate = doc.get<String?>(startingDateKey)
        return UserEntity(
            id = id,
            name = name,
            password = password,
            age = age,
            role = role,
            active = status ?: false,
            avatar = imageUrl ?: "",
            phone = phoneNumber ?: "",
            address = address ?: "",
            startingDate = startingDate ?: ""
        )

    }

    override suspend fun addImageToFirebaseStorage(
        image: File,
        userId: String
    ): NetworkResult<String> {
        return try {
            val imageRef = imagesStorageRef.child(userId)
            imageRef.putFile(image)
            val imageUrl = imageRef.getDownloadUrl()
            updateUserData(userId, mapOf("avatar" to imageUrl))
            NetworkResult.Success(imageUrl)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun updateUserData(
        id: String,
        data: Map<String, Any>
    ): NetworkResult<Boolean> {
        try {
            usersFireStore.document(id).update(data)
        } catch (e: Exception) {
            print(e.message)
            return NetworkResult.Success(false)
        }
        return NetworkResult.Success(true)
    }

    override suspend fun setUserData(id: String, data: Map<String, Any>): NetworkResult<Boolean> {
        try {
            usersFireStore.document(id).set(data)
        } catch (e: Exception) {
            print(e.message)
            return NetworkResult.Success(false)
        }
        return NetworkResult.Success(true)
    }

    override suspend fun updateCurrentUserData(data: Map<String, Any>): NetworkResult<Boolean> {
        val id = auth.currentUser?.uid ?: return NetworkResult.Error(Exception("no user"))
        try {
            usersFireStore.document(id).update(data)
        } catch (e: Exception) {
            print(e.message)
            return NetworkResult.Success(false)
        }
        return NetworkResult.Success(true)
    }

    override suspend fun changeUserStatus(status: Boolean): NetworkResult<Boolean> {
        val id = auth.currentUser?.uid ?: return NetworkResult.Error(Exception("No user"))
        return try {
            val userRef = usersFireStore.document(id)
            val data = mapOf<String, Any>("active" to status)
            userRef.update(data)
            NetworkResult.Success(true)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }


    override suspend fun getUsers(): NetworkResult<List<UserEntity>> {
        return try {
            val userResponse = usersFireStore.get()
            val users = userResponse.documents.map {
                docToUser(it)
            }

            NetworkResult.Success(users)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}