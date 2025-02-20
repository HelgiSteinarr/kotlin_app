package com.helgi.ktjpcomposeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.helgi.ktjpcomposeapp.ui.theme.KTJPComposeAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getReqToArion()

        enableEdgeToEdge()
        setContent {
            KTJPComposeAppTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp)) {
                    CurrencyConversionList()
                    // Chat(ChatData.messages)
                }
            }
        }
    }

    private val _apiData = MutableStateFlow<List<Currency>?>(null)
    val apiData: StateFlow<List<Currency>?> = _apiData
    private val _apiFailed = MutableStateFlow<Boolean>(false)
    val apiFailed: StateFlow<Boolean> = _apiFailed


    private fun getReqToArion() {
        // COROUTINE ACTION
        lifecycleScope.launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://www.arionbanki.is/Webservice/PortalCurrency.ashx?m=GetCurrencies&currencyType=AlmenntGengi&beginDate=2025-02-19&finalDate=2025-02-19&currenciesAvailable=ISK%2CUSD%2CGBP%2CEUR%2CCAD%2CDKK%2CNOK%2CSEK%2CCHF%2CJPY%2CPLN%2CXDR%2CRUB%2CZAR%2CNZD%2CHKD%2CAUD&TypeCode=A")
                    .build()
                Log.d("API " , "BEFORE DISPATCH")

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (!response.isSuccessful) {
                    Log.e("API_ERROR", "${response.code}" )
                    _apiFailed.value = true
                    return@launch
                }

                val responseBody = response.body?.string() ?: ""
                val gson = Gson()
                val listType = object : TypeToken<ArrayList<Currency?>?>() {}.type
                if (responseBody.isEmpty()) {
                    Log.e("API", "💀 Response body is empty or null, L-TIER MOVE")
                    _apiFailed.value = true
                    return@launch
                }

                try {
                    val parsedData: List<Currency> = gson.fromJson(responseBody, listType)
                    _apiData.value = parsedData
                    Log.d("API Parse", "🔥 Data successfully parsed: $parsedData")
                } catch (e: Exception) {
                    Log.e("API Parse", "💀 FUMBLED JSON PARSING: ${e.message}")
                    _apiFailed.value = true
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "${e.message}" )
                _apiFailed.value = true
            }
        }
    }
}

data class Currency(
    @SerializedName("Ticker") val ticker: String,
    @SerializedName("BidValue") val buyPrice: Float,
    @SerializedName("AskValue") val sellPrice: Float,
    @SerializedName("CustomsRate") val customsRate: Float,
    @SerializedName("LastValueChange") val lastValueChange: Float,
    @SerializedName("MainTicker") val mainTicker: String?,
    @SerializedName("Time") val time: String,
    @SerializedName("Title") val title: String?
)

@Composable
fun CurrencyField(
    currency: Currency,
    baseCurrency: String,
    baseValue: String,
    onBaseValueChange: (String, String) -> Unit
){
    Row(modifier = Modifier
        .padding(all = 10.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painter = painterResource(R.drawable.me),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        var text by remember { mutableStateOf("yee/haw") }

        Row (modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically){
            Text(text = currency.ticker)
            Text(text = currency.buyPrice.toString())
            Text(text = currency.sellPrice.toString())

            // val convertedValue = convertCurrency(baseValue.toDoubleOrNull() ?: 0.0, baseCurrency, currency.ticker)

            OutlinedTextField(

                value = text,
                onValueChange = {
                    text = it  // Update local text field
                    onBaseValueChange(it, currency.ticker)  // Pass the new value and the currency code to the parent composable
                },
                label = { Text(currency.ticker) },
                modifier = Modifier.width(100.dp)
            )
        }

    }
}

@Composable
fun CurrencyConversionList() {
    val mainActivity = LocalContext.current as? MainActivity ?:
    return Text("Unable to access MainActivity");

    val apiData by mainActivity.apiData.collectAsState()
    val apiFailed by mainActivity.apiFailed.collectAsState()

    Log.d("after api!", apiData.toString())
    
    if(apiData == null && !apiFailed) {
        Text("waiting for data...")
    } else if (apiFailed) {
        Text("FAILED TO FETCH DATA!")
    }

    var baseCurrency by remember { mutableStateOf("USD") }  // Default currency
    var baseValue by remember { mutableStateOf("1.0") }     // Default input

    LazyColumn {
        items(apiData ?: emptyList()) {currency ->
            CurrencyField(currency,
                baseCurrency = baseCurrency,
                baseValue = baseValue,
                onBaseValueChange = { newValue, newCurrency ->
                    baseValue = newValue
                    baseCurrency = newCurrency
                }
            )
        }
    }
}

fun convertCurrency(apiData: List<Currency>, amount: Double, from: String, to: String) {

}



data class Message(val author: String, val body: String)

@Composable
fun Chat(messages: List<Message>){
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(msg: Message) {
    Row (modifier = Modifier.padding(all = 8.dp)){
        Image(
            painter = painterResource(R.drawable.me),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)

        )
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )
        Column (modifier = Modifier.clickable{
            isExpanded = !isExpanded
        }) {
            Text(text = msg.author,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))

            Surface(shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp),
                color = surfaceColor
            ) {
                Text(text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if(isExpanded) Int.MAX_VALUE else 1
                )
            }

        }
    }
}
