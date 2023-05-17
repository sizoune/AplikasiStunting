package com.kominfotabalong.simasganteng.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedTextFieldComp(
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholderText: String,
    query: String,
    minLines: Int = 1,
    isError: Boolean = false,
    errorMsg: String = "",
    onQueryChange: (String) -> Unit,
) {
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
        OutlinedTextField(
            value = query,
            singleLine = singleLine,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            minLines = minLines,
            prefix = prefix,
            supportingText = supportingText,
            suffix = suffix,
            interactionSource = interactionSource,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            onValueChange = onQueryChange,
            trailingIcon = {
                if (isError)
                    Icon(Icons.Filled.Info, "Error", tint = MaterialTheme.colorScheme.error)
                else
                    trailingIcon?.let { it() }
            },
            leadingIcon = leadingIcon,
            colors = TextFieldDefaults.colors(
                cursorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                focusedLabelColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                focusedIndicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            ),
            label = {
                Text(placeholderText)
            },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        )
        if (isError)
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
    }

}