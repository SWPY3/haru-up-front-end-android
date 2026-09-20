package com.swyp.haruup.core.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 화면 구현 전까지 쓰는 임시 화면입니다. 실제 화면을 구현하면서 하나씩 제거합니다.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        if (buttonText != null && onButtonClick != null) {
            Button(onClick = onButtonClick) { Text(buttonText) }
        }
    }
}
