package app.data.work

import app.data.network.FirebaseBaseNetworkApi
import app.data.network.NetworkResult
import extensions.managerKey
import extensions.roomNumberKey
import extensions.safeLet
import extensions.startedAtKey
import kotlinx.coroutines.flow.first

class CurrentWorkFirebaseApi() : CurrentWorksApi, FirebaseBaseNetworkApi() {
    override suspend fun provideCurrentWorks(): NetworkResult<List<WorkEntity>> {
        val flow = database.reference("works").valueEvents
        val dataSnapshot = flow.first()
        val works = dataSnapshot.children.mapNotNull { data ->
            safeLet(
                (data.child(startedAtKey).value as? String),
                (data.child(managerKey).value as? String),
                data.key,
                (data.child(roomNumberKey).value as? String)) { startedAt, manager, worker, room ->
                WorkEntity(startedAt, endAt = null, manager = manager, worker = worker, room = room)
            }
        }
        return NetworkResult.Success(works)
    }
}