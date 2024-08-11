package app.presentation.usersList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.user.WorkHistory
import app.presentation.theme.mainGreen
import extensions.dateWithWord
import extensions.toDate

@Composable
fun WorkerHistoryCardView(
    history: List<WorkHistory>,
    message: String,
    workerStartingDate: String,
    onExpand: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(16.dp)
            )
            .border(
                BorderStroke(1.dp, Color.LightGray),
                RoundedCornerShape(16.dp)
            )
    ) {
        val isExpanded = remember { mutableStateOf(false) }
        val expandIcon = rememberVectorPainter(
            if (isExpanded.value)
                Icons.Default.ExpandLess
            else
                Icons.Default.ExpandMore
        )
        Column {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(workerStartingDate.dateWithWord())
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    onClick = {
                        isExpanded.value = isExpanded.value.not()
                        if (isExpanded.value) {
                            onExpand()
                        }
                    }
                ) {
                    Icon(
                        tint = Color.Black,
                        modifier = Modifier.size(25.dp),
                        painter = expandIcon,
                        contentDescription = null
                    )
                }

            }
            if (isExpanded.value) {
                AnimatedVisibility(
                    visible = isExpanded.value,
                    enter = fadeIn(initialAlpha = 0.0f) + slideInVertically(
                        tween(
                            durationMillis = 600,
                            delayMillis = 600,
                            easing = FastOutSlowInEasing
                        ),
                        initialOffsetY = { it * 6 }
                    )
                ) {
                    Column {
                        if (history.isNotEmpty()) {
                            history.forEach { history ->
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    multicolorText("Սենյակ", history.room)
                                    multicolorText(
                                        "Մենեջեր",
                                        history.requestedManager?.name ?: "Unknown"
                                    )
                                    multicolorText("Սկսվել է", history.startedAt.toDate())
                                    multicolorText("Ավարտվել է", history.endAt.toDate())
                                    if (history.numberOfStar > 0) {
                                        multicolorText(
                                            "Գնահատական ",
                                            history.numberOfStar.toString(),
                                            firstColor = if (history.numberOfStar < 3) Color.Red else if (history.numberOfStar < 5) mainGreen else Color.Green
                                        )
                                    }
                                    if (history.message.isNotEmpty()) {
                                        multicolorText("Հաղորդագրություն ", history.message)
                                    }
                                }
                                Divider(modifier = Modifier.fillMaxWidth().height(1.dp))

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (message.isNotEmpty()) {
                                    Text(message, color = mainGreen)
                                } else {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(backgroundColor = mainGreen),
                                        onClick = {
                                            onClick()
                                        }
                                    ) {
                                        Text("Տեսնել ավելին", color = Color.White)
                                    }
                                }
                            }
                        } else {
                            if (message.isNotEmpty()) {
                                Text(message, color = mainGreen)
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.width(32.dp),
                                        color = mainGreen,
                                        backgroundColor = Color.LightGray,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun multicolorText(
    first: String,
    second: String,
    firstColor: Color = mainGreen,
    secondColor: Color = Color.Black,
    fontSize: Float = 16F,
    spaceBetween: Boolean = false
) {
    Row(
        horizontalArrangement = if (spaceBetween) Arrangement.spacedBy(2.dp) else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
    ) {
        Text(
            text = "$first: ",
            color = firstColor,
            textAlign = TextAlign.Left,
            fontSize = TextUnit(fontSize, TextUnitType.Sp)
        )
        Text(
            text = second,
            color = secondColor,
            textAlign = TextAlign.Left,
            fontSize = TextUnit(fontSize, TextUnitType.Sp)
        )
    }
}