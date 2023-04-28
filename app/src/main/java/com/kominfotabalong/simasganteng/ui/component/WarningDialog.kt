package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.ui.theme.Yellow400

@Composable
fun WarningDialog(
    showDialog: Boolean,
    dialogDesc: String,
    onDismiss: (Boolean) -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog)
        Dialog(onDismissRequest = { onDismiss(false) }) {
            Card(elevation = CardDefaults.cardElevation(10.dp), shape = RoundedCornerShape(20.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .width(250.dp)
                        .wrapContentSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Yellow400)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_warning),
                            contentDescription = "perhatian",
                            modifier = modifier.size(48.dp)
                        )
                        Text(
                            text = "Perhatian",
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = modifier.padding(top = 4.dp)
                        )
                    }
                    Text(
                        text = dialogDesc,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = modifier.padding(16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = { onDismiss(false) }
                        ) {
                            Text(color = Color.Red, text = "Batal")
                        }
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onOkClick()
                                onDismiss(false)
                            }
                        ) {
                            Text(
                                "Ya",
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

        }
}