package app.data.work

import app.data.network.NetworkResult

interface CurrentWorksApi {
    suspend fun provideCurrentWorks(): NetworkResult<List<WorkEntity>>
}