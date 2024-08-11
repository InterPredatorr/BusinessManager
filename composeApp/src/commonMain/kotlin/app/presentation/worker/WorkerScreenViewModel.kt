package app.presentation.worker

import app.domain.user.WorkData
import app.domain.user.WorksUseCase
import app.domain.user.needToApprove
import extensions.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WorkerScreenViewModel(private val worksUseCase: WorksUseCase) {
    var timer = Timer()

    suspend fun listenForWork(): Flow<List<WorkData>> {
        return worksUseCase.listenForWork().map { w ->
            w.filter {
                !it.needToApprove()
            }
        }
    }

    fun startWork(workId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            worksUseCase.startWork(workId)
        }
    }

    fun moveWorkToReadyToApprove(workId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            worksUseCase.moveWorkToReadyToApprove(workId = workId)
        }
    }
}

