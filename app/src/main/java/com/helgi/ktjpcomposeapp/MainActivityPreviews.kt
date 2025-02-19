package com.helgi.ktjpcomposeapp

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.helgi.ktjpcomposeapp.ui.theme.KTJPComposeAppTheme

@Preview(
    name = "Light mode",
    showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun MessageCardPreview() {
    KTJPComposeAppTheme {
        Surface() {
            MessageCard(Message(author = "MONKEH", body = "turn the fucking heater on people"))
        }
    }
}

@Preview (name = "Chat light mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Chat Dark Mode"
)
@Composable
fun ChatPreview() {
    KTJPComposeAppTheme {
        Surface() {
            Chat(ChatData.messages)
        }
    }
}