package app.presentation.usersList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.data.imagePicker.toFile
import app.presentation.login.LoginTextField
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


class UserProfileScreen(
    private val userId: String,
    val name: String,
    val age: Int,
    val phone: String,
    val address: String,
    private val imageUrl: String,
    var onChange: () -> Unit = {}
) : Screen {
    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: UsersListViewModel = koinInject()
        val resizeOptions = ResizeOptions(
            width = 512,
            height = 512,
            resizeThresholdBytes = 2 * 512 * 512
        )
        val singleImagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = rememberCoroutineScope(),
            resizeOptions = resizeOptions,
            onResult = { byteArrays ->
                coroutineScope.launch {
                    byteArrays.firstOrNull()?.let {
                        toFile(it)?.let { file ->
                            viewModel.addUserImage(file, userId) { succeed ->
                                if (succeed)  {
                                    onChange()
                                    bottomSheetNavigator.hide()
                                }
                            }
                        }
                    }
                }
            }
        )

        val changingUserData = remember { SnapshotStateMap<String, Any>() }
        var updateUserData by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = updateUserData) {
            if (updateUserData) {
                viewModel.updateUserData(userId, changingUserData) {
                    this@UserProfileScreen.onChange()
                    bottomSheetNavigator.hide()
                    updateUserData = false
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            this.item {
                profileImage(imageUrl) {
                    singleImagePicker.launch()
                }
                Text(
                    name,
                    fontSize = TextUnit(30F, TextUnitType.Sp),
                    fontWeight = FontWeight.Black
                )
                LoginTextField(
                    (changingUserData["name"] ?: name).toString(),
                    "Անուն",
                    onValueChange = { changingUserData["name"] = it }
                )
                LoginTextField(
                    (changingUserData["age"] ?: age).toString(),
                    "Տարիք",
                    keyboardType = KeyboardType.Number,
                    onValueChange = { changingUserData["age"] = it })
                LoginTextField(
                    (changingUserData["address"] ?: address).toString(),
                    "Հասցե",
                    onValueChange = { changingUserData["address"] = it })
                LoginTextField(
                    (changingUserData["phone"] ?: phone).toString(),
                    "Հեռախոս",
                    onValueChange = { changingUserData["phone"] = it })
                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = mainGreen),
                    onClick = { updateUserData = true }
                ) {
                    Text("Թարմացնել տվյալները",
                        color = Color.White)
                }
            }
        }
    }
}

@Composable
fun profileImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val personIcon = rememberVectorPainter(Icons.Filled.Person)

    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(imageUrl)
            .build(),
        contentDescription = null,
        error = personIcon,
        modifier = modifier
            .width(200.dp)
            .height(200.dp)
            .fillMaxSize()
            .border(BorderStroke(0.3.dp, Color.LightGray), RoundedCornerShape(CornerSize(20.dp)))
            .clip(RoundedCornerShape(CornerSize(20.dp)))
            .clickable { onClick() },
        contentScale = ContentScale.Crop
    )

}