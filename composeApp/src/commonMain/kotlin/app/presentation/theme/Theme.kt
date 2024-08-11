package app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val mainGreen = Color(0xFF6FDA44)
val errorColor = Color(0xFFFF0000)

private val colorScheme = lightColorScheme(
    primary = mainGreen
)


@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme
    ) {
        content()
    }
}