package com.kominfotabalong.simasganteng.ui.screen.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kominfotabalong.simasganteng.ui.component.OutlinedSpinner

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    showFilter: Boolean,
    onFilterChoose: (String) -> Unit,
    onDismiss: () -> Unit
) {

    if (showFilter)
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                elevation = CardDefaults.cardElevation(10.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier.navigationBarsPadding()
            ) {
                var filterError by remember { mutableStateOf(false) }
                var selectedFilter by remember {
                    mutableStateOf("")
                }

                fun validateInput(): Boolean {
                    if (selectedFilter == "") {
                        filterError = true
                        return false
                    }
                    return true
                }

                IconButton(
                    onClick = {
                        onDismiss()
                    },
                    modifier = modifier.align(Alignment.End)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "tutup")
                }

                Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    OutlinedSpinner(
                        options = listOf(
                            "Berat Badan per Usia",
                            "Tinggi / Panjang Badan per Usia",
                            "Berat Badan per Tinggi / Panjang Badan"
                        ),
                        label = "Pilih Filter Sebaran",
                        value = selectedFilter,
                        singleLine = true,
                        isError = filterError,
                        errorMsg = "Toloh pilih Filter Sebaran dahulu!",
                        onOptionSelected = {
                            selectedFilter = it
                        },
                        modifier = modifier
                    )

                    Button(
                        onClick = {
                            if (validateInput()) {
                                onFilterChoose(selectedFilter)
                                onDismiss()
                            }
                        }, modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Filter",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }

            }
        }
}