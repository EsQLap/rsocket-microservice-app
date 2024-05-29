package com.example.rsocketapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rsocketapp.system_connection.SystemConnectionViewModel
import com.example.rsocketapp.rsocket.RSocketViewModel
import kotlinx.coroutines.launch

object MainScreen {

    @Composable
    fun View() {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val systemConnectionViewModel = viewModel<SystemConnectionViewModel>()
        val rSocketViewModel = viewModel<RSocketViewModel>()
        LaunchedEffect(Unit) {
            systemConnectionViewModel.connectivityState.collect {
                if (it.isAvailable) {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    rSocketViewModel.connect()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Disconnected",
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    var ntlkText by remember { mutableStateOf("") }
                    TextField(
                        value = ntlkText,
                        onValueChange = { ntlkText = it },
                        label = {
                            Text(text = "Текст для определения частей речи слов")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val connectivityState by rSocketViewModel.connectionStateFlow.collectAsState()
                    Button(onClick = {
                        rSocketViewModel.nltkSearch(ntlkText)
                    }, enabled = connectivityState.isConnected && ntlkText.isNotBlank()) {
                        Text(text = "Узнать части речи")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Результат")
                    Spacer(modifier = Modifier.height(8.dp))
                    val textState by rSocketViewModel.nltkStateFlow.collectAsState()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(textState) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Слово: ${it.word}")
                                    Text(text = "Часть речи: ${it.speechPart}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen.View()
}