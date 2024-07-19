package com.example.chronicle.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chronicle.response.ChatRequest
import com.example.chronicle.response.ChatResponse
import com.example.chronicle.response.Message
import com.example.chronicle.utils.ApiInterface
import com.example.chronicle.utils.CHAT_GPT_MODEL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatBotScreen(
    navController: NavController,
    apiInterface: ApiInterface,
    context: Context = LocalContext.current
) {
    var messageText by remember { mutableStateOf("") }
    var lastResponse by remember { mutableStateOf("") }
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat with AI") }, actions = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Go Back")
                }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // support long click
            Text(
                text = "AI Response: $lastResponse",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        val clip = ClipData.newPlainText("AI Response", lastResponse)
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Response copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type your message...") },
                singleLine = false
            )
            Button(
                onClick = {
                    if (messageText.isNotEmpty()) {
                        sendMessageToChatGPT(apiInterface, messageText) { response ->
                            lastResponse = response
                        }
                        messageText = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Send")
            }
        }
    }
}

fun sendMessageToChatGPT(apiInterface: ApiInterface, message: String, onResult: (String) -> Unit) {
    val chatRequest = ChatRequest(
        messages = listOf(Message(content = message, role = "user")),
        model = CHAT_GPT_MODEL
    )
    apiInterface.createChatCompletion(chatRequest).enqueue(object : Callback<ChatResponse> {
        override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content?.let(onResult)
                Log.e(
                    "api successful",
                    response.body()?.choices?.firstOrNull()?.message?.content.toString()
                )
            } else {
                Log.e("API Error", "Failed with ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
            Log.e("API Error", "Failed with error $t")
        }
    })
}