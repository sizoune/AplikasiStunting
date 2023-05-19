package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.theme.BlueGrey800

@Composable
fun StuntingDialog(
    showDialog: Boolean,
    dialogDesc: String,
    onDismiss: (Boolean) -> Unit,
    onPhoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.warning))

    if (showDialog)
        Dialog(onDismissRequest = { }) {
            Card(elevation = CardDefaults.cardElevation(10.dp), shape = RoundedCornerShape(20.dp)) {
                ConstraintLayout(
                    modifier = modifier
                        .widthIn(min = 200.dp)
                        .heightIn(min = 250.dp)
                        .background(BlueGrey800)
                ) {
                    val (closeBtn, title, descText, lottie, btnStaff) = createRefs()

                    IconButton(
                        onClick = {
                            onDismiss(false)
                        },
                        modifier = modifier.constrainAs(closeBtn) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "tutup",
                            tint = Color.LightGray
                        )
                    }

                    LottieAnimation(
                        composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = modifier
                            .size(100.dp)
                            .constrainAs(lottie) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(top = 16.dp)
                    )

                    Text(
                        text = "Perhatian",
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = modifier
                            .constrainAs(title) {
                                top.linkTo(lottie.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(top = 4.dp)
                    )

                    Text(
                        text = dialogDesc,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = modifier
                            .constrainAs(descText) {
                                top.linkTo(title.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(16.dp)
                    )

                    Button(
                        onClick = {
                            onPhoneClick()
                        }, modifier = modifier
                            .constrainAs(btnStaff) {
                                top.linkTo(descText.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                            .padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "Hubungi Petugas",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }


                }
            }

        }
}