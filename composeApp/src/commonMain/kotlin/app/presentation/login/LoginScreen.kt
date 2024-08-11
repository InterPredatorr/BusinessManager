package app.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import app.presentation.splash.NextAction
import app.presentation.theme.mainGreen
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

@Composable
fun LoginScreen(isLoggedIn: MutableState<NextAction>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginError: MutableState<String> = remember { mutableStateOf("") }

    val loginViewModel: LoginViewModel = koinInject()
    loginViewModel.initViewModel(isLoggedIn, loginError)

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Մուտք գործել",
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(40F, TextUnitType.Sp),
                color = mainGreen
            )
            LoginTextField(
                value = username,
                label = "Մուտքանուն",
                errorText = loginError.value,
                keyboardType = KeyboardType.Text,
                onValueChange = {
                    username = it
                    loginError.value = ""
                },
            )

            LoginTextField(
                value = password,
                label = "Գաղտնաբառ",
                errorText = loginError.value,
                keyboardType = KeyboardType.Password,
                onValueChange = {
                    loginError.value = ""
                    password = it
                },
            )

            Button(
                modifier = Modifier
                    .padding(vertical = 15.dp),
                content = { Text("Մուտք", color = Color.White) },
                colors = ButtonDefaults.buttonColors(backgroundColor = mainGreen),
                onClick = { loginViewModel.login(username, password) }
            )
        }
    }
}