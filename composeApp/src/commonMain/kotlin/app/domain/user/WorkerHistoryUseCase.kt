package app.domain.user

import app.data.network.NetworkResult
import app.data.user.UserHistoryApi

interface WorkerHistoryUseCase {
    suspend fun provideUserHistory(userId: String, date: String, count: Int): NetworkResult<List<WorkHistory>>
    suspend fun loadMore(userId: String, date: String , count:Int): NetworkResult<List<WorkHistory>>
}

class WorkerHistoryUseCaseImpl(private val userHistoryApi: UserHistoryApi, private val userMapper: UserMapper) : WorkerHistoryUseCase {
    override suspend fun provideUserHistory(userId: String, date: String, count: Int): NetworkResult<List<WorkHistory>> {
        return when (val result = userHistoryApi.getUserHistory(userId, date, count)) {
            is NetworkResult.Success -> {
                NetworkResult.Success(userMapper.userHistoryEntityToUserHistoryMapper.map(result.data))
            }

            is NetworkResult.Error -> {
                NetworkResult.Error(result.exception)
            }
        }
    }

    override suspend fun loadMore(userId: String, date: String, count: Int): NetworkResult<List<WorkHistory>> {
        return when (val result = userHistoryApi.loadMore(userId, date, count)) {
            is NetworkResult.Success -> {
                NetworkResult.Success(userMapper.userHistoryEntityToUserHistoryMapper.map(result.data))
            }
            is NetworkResult.Error -> {
                NetworkResult.Error(result.exception)
            }
        }
    }
}
