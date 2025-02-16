package app.presentation

import app.presentation.main.App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import android.content.Intent
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.permission.permissionUtil

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()

        NotifierManager.onCreateOrOnNewIntent(intent)
        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        NotifierManager.onCreateOrOnNewIntent(intent)
    }
}





@Preview
@Composable
fun AppAndroidPreview() {
    App()
}