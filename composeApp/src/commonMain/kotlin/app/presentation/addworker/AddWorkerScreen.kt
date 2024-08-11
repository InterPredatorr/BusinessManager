package app.presentation.addworker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.user.UserType
import app.domain.user.armToUserType
import app.presentation.login.LoginTextField
import app.presentation.login.dropdownMenuBox
import app.presentation.theme.errorColor
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

class AddWorkerScreen : Screen {
    @Composable
    override fun Content() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var ageSliderValue by remember { mutableStateOf(20f) }

        var userTypeErrorText by remember { mutableStateOf("") }
        var nameErrorText by remember { mutableStateOf("") }
        var passwordErrorText by remember { mutableStateOf("") }

        var addWorkerErrorText by remember { mutableStateOf("") }
        var addWorkerSuccessText by remember { mutableStateOf("") }

        val selectedText: MutableState<String> = remember { mutableStateOf("Ընտրեք աշխատողի տեսակը") }

        val addWorkerViewModel: AddWorkerViewModel = koinInject()

        LaunchedEffect(true) {
            addWorkerViewModel.addWorkerResult.collectLatest { state ->
                if (state) {
                    addWorkerSuccessText = "Աշխատողը հաջողությամբ ավելացվեց"
                    addWorkerErrorText = ""
                } else {
                    addWorkerErrorText = "Չհաջողվեց ավելացնել աշխատողը"
                    addWorkerSuccessText = ""
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Ավելացնել նոր աշխատող",
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(16F, TextUnitType.Sp),
                    color = mainGreen
                )
                LoginTextField(
                    value = username,
                    label = "Մուտքանուն",
                    errorText = nameErrorText,
                    keyboardType = KeyboardType.Text,
                    onValueChange = {
                        username = it
                        nameErrorText = ""
                    },
                )

                LoginTextField(
                    value = password,
                    label = "Գաղտնաբառ",
                    errorText = passwordErrorText,
                    keyboardType = KeyboardType.Password,
                    onValueChange = {
                        passwordErrorText = ""
                        password = it
                    },
                )

                Slider(
                    value = ageSliderValue,
                    onValueChange = { newValue ->
                        ageSliderValue = newValue
                    },
                    onValueChangeFinished = {},
                    valueRange = 18f..100f,
                    steps = 0
                )
                Text(text = "Տարիք = ${ageSliderValue.toInt()}")

                dropdownMenuBox(listOf(UserType.Worker.getArmName(), UserType.Manager.getArmName(), UserType.Owner.getArmName()), selectedText)

                Button(
                    modifier = Modifier,
                    content = { Text("Ավելացնել", color = Color.White) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = mainGreen),
                    onClick = {
                        addWorkerErrorText = ""
                        addWorkerSuccessText = ""
                        val userType = selectedText.value.armToUserType() ?: run {
                            userTypeErrorText = "Ընտրեք օգտվողի տեսակը"
                            return@Button
                        }

                        val name = username.takeIf { it.isNotEmpty() } ?: run {
                            nameErrorText = "Գրեք աշխատողի անունը"
                            return@Button
                        }

                        val pass = password.takeIf { it.isNotEmpty() } ?: run {
                            passwordErrorText = "Գրեք աշխատողի գաղտնաբառը"
                            return@Button
                        }

                        addWorkerViewModel.addWorker(name, pass, ageSliderValue.toInt(), userType)
                    }
                )
                addWorkerErrorText.takeIf { it.isNotEmpty() }?.let { Text(text = it, color = errorColor) }
                addWorkerSuccessText.takeIf { it.isNotEmpty() }?.let { Text(text = it, color = mainGreen) }
            }
        }
    }
}