package com.helgi.ktjpcomposeapp

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        Surface(modifier = Modifier.padding(top = 20.dp)) {
            Chat(ChatData.messages)
        }
    }
}

@Preview
@Composable
fun CurrencyFieldPreview() {
    var isk = Currency(ticker = "ISK", buyPrice = 1.0f, sellPrice = 1.0f, customsRate = 1.0f, lastValueChange = 0.1f, time = "dsa", title = "", mainTicker = "")
    KTJPComposeAppTheme {
        Surface() {
            
        }
    }
}

@Preview
@Composable
fun CurrencyListPreview() {
    var isk = Currency(ticker = "ISK", buyPrice = 1.0f, sellPrice = 1.0f, customsRate = 1.0f, lastValueChange = 0.1f, time = "dsa", title = "", mainTicker = "")

    KTJPComposeAppTheme {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)) {

            CurrencyConversionList()

        }
    }
}

