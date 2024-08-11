@file:OptIn(ExperimentalMaterial3Api::class)

package app.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.presentation.theme.errorColor
import app.presentation.theme.mainGreen

@Composable
fun LoginTextField(
    value: String,
    label: String,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {

    val textFieldVisualTransformation =
        if (keyboardType==KeyboardType.Password)
            PasswordVisualTransformation()
        else
            VisualTransformation.None


    OutlinedTextField(
        modifier = Modifier
            .padding(top = 25.dp),
        shape = RoundedCornerShape(20),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        isError = errorText?.takeIf { it.isNotEmpty() }!=null,
        label = { Text(label) },
        visualTransformation = textFieldVisualTransformation,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = mainGreen,
            focusedLabelColor = mainGreen
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    errorText?.takeIf { it.isNotEmpty() }?.let {
        Text(errorText, color = errorColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropdownMenuBox(items: List<String>, selectedText: MutableState<String>) {
    val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = {
                expanded.value = !expanded.value
            }
        ) {
            TextField(
                value = selectedText.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText.value = item
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}