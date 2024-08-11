package app.domain.user

import app.data.user.UserHistoryEntity
import extensions.SuspendMapper
import kotlinx.datetime.toInstant

class UserMapper(val userProviderUseCase: UserProviderUseCase) {
    val userHistoryEntityToUserHistoryMapper =
        object : SuspendMapper<UserHistoryEntity, WorkHistory> {
            override suspend fun map(s: UserHistoryEntity): WorkHistory {
                return WorkHistory(
                    room = s.room,
                    requestedManager = userProviderUseCase.getUser(s.requestedManager),
                    approvedManager = userProviderUseCase.getUser(s.approvedManager),
                    startedAt = s.startAt.toInstant(),
                    requestedAt = s.requestedAt.toInstant(),
                    endAt = s.endAt.toInstant(),
                    approvedAt = s.approvedAt.toInstant(),
                    numberOfStar = s.numberOfStar ?: -1,
                    message = s.message ?: ""
                )
            }
        }
}

