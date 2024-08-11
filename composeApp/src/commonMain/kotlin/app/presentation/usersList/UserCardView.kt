package app.presentation.usersList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.user.User
import app.domain.user.UserType
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import extensions.ifTrue


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserCardView(
    user: User,
    isEditable: Boolean = true,
    isCompact: Boolean = false,
    hasButton: Boolean = true,
    border: Color? = null,
    onChange: () -> Unit = {},
    onTap: (() -> Unit)? = null
) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    val personIcon = rememberVectorPainter(Icons.Filled.Person)


    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(16.dp)
            )
            .border(
                BorderStroke(3.dp, border ?: Color.LightGray),
                RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .ifTrue(isCompact) {
                    width(50.dp)
                }
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        val isLoading = remember { mutableStateOf(false) }
                        val isFailed = remember { mutableStateOf(false) }
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(user.imageUrl)
                                .build(),
                            onError = {
                                isFailed.value = true
                                print(it.result.throwable.message)
                                print(it.result.throwable.cause)
                                print(it.result.throwable.suppressedExceptions)
                            },
                            onLoading = { isLoading.value = true },
                            onSuccess = { isLoading.value = false },
                            contentDescription = null,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(if (isCompact) 40.dp else 80.dp)
                                .height(if (isCompact) 40.dp else 80.dp)
                                .fillMaxSize()
                                .border(
                                    BorderStroke(0.5.dp, Color.LightGray),
                                    RoundedCornerShape(CornerSize(8.dp))
                                )
                                .clip(RoundedCornerShape(CornerSize(8.dp)))
                                .ifTrue(isEditable) {
                                    combinedClickable {
                                        if (onTap != null) {
                                            onTap()
                                        } else {
                                            bottomSheetNavigator.show(
                                                UserProfileScreen(
                                                    user.id, user.name, user.age,
                                                    user.phoneNumber, user.address, user.imageUrl
                                                ) {
                                                    onChange()
                                                })
                                        }
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )
                        if (isLoading.value && isFailed.value.not()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(if (isCompact) 30.dp else 50.dp),
                                color = mainGreen,
                                backgroundColor = Color.LightGray,
                            )
                        }
                        if (isFailed.value) {
                            Image(
                                personIcon,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                    if (!isCompact) {

                        Text(
                            user.name,
                            modifier = Modifier
                                .padding(horizontal = 2.dp),
                            softWrap = true,
                            maxLines = 2,
                            fontSize = TextUnit(16F, TextUnitType.Sp)
                        )
                        Spacer(modifier = Modifier.weight(1.0f))
                        if (user.role == UserType.Worker && hasButton) {
                            Button(
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = mainGreen),
                                onClick = {
                                    bottomSheetNavigator.show(WorkerHistoryScreen(user))
                                }
                            ) {
                                Text(
                                    text = "Պատմ.",
                                    maxLines = 1,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                if (isCompact) {
                    Text(
                        user.name,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        style = TextStyle.Default,
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 2.dp),
                        fontSize = TextUnit(10F, TextUnitType.Sp)
                    )
                }
            }
        }
    }
}