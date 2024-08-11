@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")
package app.data.login

import app.data.network.FirebaseBaseNetworkApi
import app.data.network.NetworkResult

class LoginApiFirebaseImpl : LoginApi, FirebaseBaseNetworkApi() {
    override suspend fun register(name: String, password: String): NetworkResult<String> {
        val result = try {
            auth.createUserWithEmailAndPassword(createEmailByName(name), password)
        } catch (e: Exception) {
            null
        }
        return result?.user?.let { user ->
            NetworkResult.Success(user.uid)
        } ?: NetworkResult.Error(Exception("Can not login"))
    }

    override suspend fun signIn(name: String, password: String): NetworkResult<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(createEmailByName(name), password)
            result.user?.let { user ->
                NetworkResult.Success(user.uid)
            } ?: NetworkResult.Error(Exception("Can not login"))
        } catch (e: Exception) {
            NetworkResult.Error(Exception(e))
        }
    }

    override suspend fun signOut() {
        database.reference().onDisconnect()
        auth.signOut()
    }

    override suspend fun createNewWorkerAccount(name: String, password: String): NetworkResult<String> {
        val currentUserEmail = auth.currentUser?.displayName ?: return NetworkResult.Error(Exception("There is no current user"))
        val currentUserPassword = getCurrentUserPassword() ?: return NetworkResult.Error(Exception("There is no current user"))
        return when (val registerResult = register(name, password)) {
            is NetworkResult.Success -> {
                auth.signOut()
                signIn(createNameByEmail(currentUserEmail), currentUserPassword)
            }

            is NetworkResult.Error -> {
                NetworkResult.Error(registerResult.exception)
            }
        }
    }

    private suspend fun getCurrentUserPassword(): String? {
        val currentUserId = auth.currentUser?.uid ?: return null

        return try {
            val userData: Map<String, Any> = usersFireStore.document(currentUserId).get().data()

            userData["password"] as? String
        } catch (e: Exception) {
            null
        }
    }

    private fun createEmailByName(name: String): String {
        return "$name@gmail.com"
    }

    private fun createNameByEmail(email: String): String {
        val index = email.indexOfFirst {
            it.equals("@")
        }
        return email.substring(0, index)
    }
}