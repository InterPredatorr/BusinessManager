package app.presentation.rooms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.user.WorkData
import app.domain.user.isActive
import app.domain.user.needToApprove
import app.presentation.login.LoginTextField
import app.presentation.theme.errorColor
import app.presentation.theme.mainGreen
import app.presentation.usersList.multicolorText
import app.presentation.usersList.profileImage
import cafe.adriel.voyager.core.screen.Screen
import extensions.Timer


class WorkDetailScreen(
    private val workData: WorkData,
    private val timer: Timer,
    private val acceptWork: (WorkData, Int, String) -> Unit,
    private val rejectWork: (WorkData) -> Unit,
) : Screen {
    @Composable
    override fun Content() {
        val invalidate = remember { mutableStateOf(0) }
        val numberOfStars = remember { mutableStateOf(5) }
        val message = remember { mutableStateOf("") }
        val startedAt = workData.startedAt ?: Timer.Now.toString()
        val endAt = workData.endAt ?: Timer.Now.toString()
        val starIcon = rememberVectorPainter(Icons.Filled.Star)
        val approveIcon = rememberVectorPainter(Icons.Filled.Done)
        val declineIcon = rememberVectorPainter(Icons.Filled.Delete)
        val delayed = timer.calculateDeltaTimeWithMinutes(endAt, startedAt) > workData.duration - 1
        val requestedAt = workData.requestedAt

        val boarderColor = if (workData.isActive()) {
            Color.Red
        } else if (workData.needToApprove()) {
            Color.Green
        } else {
            Color.Cyan
        }

        LaunchedEffect(true) {
            timer.start {
                invalidate.value += 1
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = invalidate.value.toString(),
                        fontSize = TextUnit(1f, TextUnitType.Sp)
                    )
                    profileImage(
                        workData.worker?.imageUrl ?: "",
                        Modifier
                            .border(
                                BorderStroke(3.dp, boarderColor),
                                RoundedCornerShape(CornerSize(20.dp))
                            )
                    ) {

                    }
                }

                multicolorText(
                    "Աշխատող",
                    workData.worker?.name + "",
                    firstColor = Color.Gray,
                    fontSize = 24F,
                    spaceBetween = true
                )
                multicolorText(
                    "Մենեջեր",
                    workData.manager.name,
                    firstColor = Color.Gray,
                    fontSize = 24F,
                    spaceBetween = true
                )
                multicolorText(
                    "Սենյակ",
                    workData.room.toString(),
                    firstColor = Color.Gray,
                    fontSize = 24F,
                    spaceBetween = true
                )
                multicolorText(
                    "Տևողություն",
                    workData.duration.toString() + " ր",
                    firstColor = Color.Gray,
                    fontSize = 24F,
                    spaceBetween = true
                )
                Text(
                    "Հանձնարարվել է՝ " + timer.calculateTime(requestedAt) + " ր առաջ",
                    color = if (delayed) mainGreen else Color.Red,
                    fontSize = TextUnit(24F, TextUnitType.Sp),
                )
                if (workData.needToApprove()) {
                    multicolorText(
                        "Աշխատել է",
                        timer.calculateDeltaTime(startedAt, endAt) + " ր",
                        firstColor = Color.Gray,
                        fontSize = 24F,
                        spaceBetween = true
                    )
                    multicolorText(
                        "Ավարտելուց",
                        timer.calculateTime(endAt) + " ր",
                        firstColor = Color.Gray,
                        fontSize = 24F,
                        spaceBetween = true
                    )
                }
                if (workData.isActive()) {
                    multicolorText(
                        "Սկզբից",
                        timer.calculateTime(workData.startedAt + "") + " ր",
                        firstColor = Color.Gray,
                        fontSize = 24F,
                        spaceBetween = true
                    )
                }
                if (workData.needToApprove()) {
                    Row {
                        IconButton(
                            onClick = {
                                numberOfStars.value = 1;
                            },
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp).background(color = Color.Red),
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Person",
                                modifier = Modifier.fillMaxSize(1.0F),
                                tint = if (numberOfStars.value >= 1) Color.Yellow else Color.White
                            )
                        }
                        IconButton(
                            onClick = { numberOfStars.value = 2; },
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp).background(color = Color.Red),
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Person",
                                modifier = Modifier.fillMaxSize(1.0F),
                                tint = if (numberOfStars.value >= 2) Color.Yellow else Color.White
                            )

                        }
                        IconButton(
                            onClick = { numberOfStars.value = 3; },
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp).background(color = Color.Red),
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Person",
                                modifier = Modifier.fillMaxSize(1.0F),
                                tint = if (numberOfStars.value >= 3) Color.Yellow else Color.White
                            )

                        }
                        IconButton(
                            onClick = { numberOfStars.value = 4; },
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp).background(color = Color.Red),
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Person",
                                modifier = Modifier.fillMaxSize(1.0F),
                                tint = if (numberOfStars.value >= 4) Color.Yellow else Color.White
                            )

                        }
                        IconButton(
                            onClick = { numberOfStars.value = 5; },
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp).background(color = Color.Red),
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Person",
                                modifier = Modifier.fillMaxSize(1.0F),
                                tint = if (numberOfStars.value >= 5) Color.Yellow else Color.White
                            )

                        }
                    }
                    LoginTextField(
                        message.value,
                        "Գնահատական",
                        onValueChange = { message.value = it })
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {


                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            onClick = {
                                acceptWork.invoke(workData, numberOfStars.value, message.value)
                            }
                        ) {
                            Image(
                                painter = approveIcon,
                                colorFilter = ColorFilter.tint(mainGreen),
                                contentDescription = "Icon",
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            onClick = {
                                rejectWork.invoke(workData)
                            }
                        ) {
                            Image(
                                painter = declineIcon,
                                colorFilter = ColorFilter.tint(errorColor),
                                contentDescription = "Icon"
                            )
                        }

                    }
                }
            }
        }
    }
}