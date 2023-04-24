package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedSpinner(
    modifier: Modifier = Modifier,
    options: List<String>,
    label: String,
    value: String = "",
    isError: Boolean = false,
    errorMsg: String = "",
    onOptionSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if (options.isNotEmpty()) options[0] else label) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = if (isError) {
                    0.dp
                } else {
                    10.dp
                }
            )
            .heightIn(min = 48.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
        ) {
            OutlinedTextField(
                readOnly = true,
                value = value,
                onValueChange = { selectedOptionText = it },
                label = { Text(label) },
                isError = isError,
                trailingIcon = {
                    if (isError)
                        Icon(Icons.Filled.Info, "Error", tint = MaterialTheme.colorScheme.error)
                    else
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = modifier
                    .fillMaxWidth(1f)
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOptionText = selectionOption
                            onOptionSelected(selectionOption)
                            expanded = false
                        },
                        text = {
                            Text(text = selectionOption)
                        }
                    )
                }
            }
        }
        if (isError)
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
    }

}