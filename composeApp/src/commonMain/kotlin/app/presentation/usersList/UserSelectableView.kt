package app.presentation.usersList

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.domain.user.User
import app.presentation.theme.mainGreen

@Composable
fun UserSelectableView(
    user: User,
    isSelectable: Boolean = true,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onImageChange: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        UserCardView(
            user,
            hasButton = false,
            isEditable = false,
            border = if (isSelected) mainGreen else null,
            onChange = onImageChange)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (isSelectable) {
                RadioButton(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = mainGreen,
                        unselectedColor = Color.LightGray
                    )
                )
            }
        }
    }

}