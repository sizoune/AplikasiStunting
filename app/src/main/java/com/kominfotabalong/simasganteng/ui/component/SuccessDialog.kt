package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.theme.BlueGrey800
import com.kominfotabalong.simasganteng.ui.theme.SIMASGANTENGTheme

@Composable
fun SuccessDialog(
    showDialog: Boolean,
    dialogDesc: String,
    onDismiss: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))

    if (showDialog)
        Dialog(onDismissRequest = { onDismiss(false) }) {
            Card(elevation = CardDefaults.cardElevation(10.dp), shape = RoundedCornerShape(20.dp)) {
                ConstraintLayout(
                    modifier = modifier
                        .widthIn(min = 200.dp, max = 220.dp)
                        .heightIn(min = 250.dp)
                        .background(BlueGrey800)
                ) {
                    val (closeBtn, title, descText, lottie) = createRefs()

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
                        modifier = modifier
                            .size(100.dp)
                            .constrainAs(lottie) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                    )

                    Text(
                        text = "Sukses",
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
                }
            }

        }
}

@Preview
@Composable
fun SuccessDialogPrev() {
    SIMASGANTENGTheme {
        SuccessDialog(
            showDialog = true,
            dialogDesc = "Terima kasih atas laporan anda!, sistem kami mendeteksi bahwa anak anda TIDAK tersuspek Stunting!",
            onDismiss = {

            })
    }
}