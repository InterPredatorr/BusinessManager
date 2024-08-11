package app.data.login
import app.data.network.FirebaseBaseNetworkApi
import app.data.network.NetworkResult

interface LoginApi {
    suspend fun register(name: String, password: String): NetworkResult<String>

    suspend fun signIn(name: String, password: String): NetworkResult<String>

    suspend fun signOut()

    suspend fun createNewWorkerAccount(name: String, password: String): NetworkResult<String>
}