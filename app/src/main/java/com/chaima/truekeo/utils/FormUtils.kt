package com.chaima.truekeo.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.R

// Texto de error para campos de formulario
@Composable
fun FormErrorText(
    showError: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    if (!showError) return

    Text(
        text = message,
        style = MaterialTheme.typography.labelSmall,
        fontFamily = FontFamily(Font(R.font.saira_regular)),
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
    )
}