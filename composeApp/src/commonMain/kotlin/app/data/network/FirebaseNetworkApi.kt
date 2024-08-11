package app.data.network

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.storage.StorageReference
import dev.gitlive.firebase.storage.storage

abstract class FirebaseBaseNetworkApi : NetworkApi {
    protected val fireStore = Firebase.firestore
    protected val usersFireStore = fireStore.collection("USERS")
    protected val imagesStorageRef: StorageReference = Firebase.storage.reference.child("images")
    protected val functions = Firebase.functions
    protected val database = Firebase.database
    protected val auth = Firebase.auth
}