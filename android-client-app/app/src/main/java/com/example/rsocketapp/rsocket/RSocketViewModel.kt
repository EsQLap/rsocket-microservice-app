package com.example.rsocketapp.rsocket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rsocketapp.jackson.Jackson
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.transport.ktor.tcp.TcpClientTransport
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RSocketViewModel : ViewModel() {

    private val _connectionStateFlow = MutableStateFlow(RSocketConnectionState(false))
    private val transport =
        TcpClientTransport(
            "10.0.2.2",
            7000,
            CoroutineExceptionHandler { _, _ ->
                _connectionStateFlow.update { it.copy(isConnected = false) }
            })
    private val connector = RSocketConnector {
        connectionConfig {
            payloadMimeType = PayloadMimeType(
                data = WellKnownMimeType.ApplicationJson,
                metadata = WellKnownMimeType.MessageRSocketCompositeMetadata,
            )
        }
    }
    private val requestSharedFlow = MutableSharedFlow<suspend (RSocket) -> Unit>()
    private val _nltkStateFlow = MutableStateFlow<List<WordSpeechPart>>(emptyList())
    private val _nltkErrorStateFlow = MutableStateFlow<Throwable?>(null)
    val connectionStateFlow = _connectionStateFlow.asStateFlow()
    val nltkStateFlow = _nltkStateFlow.asStateFlow()
    val nltkErrorStateFlow = _nltkErrorStateFlow.asStateFlow()

    fun connect() {
        viewModelScope.launch {
            val rSocket = connector.connect(transport)
            rSocket.launch {
                _connectionStateFlow.update { it.copy(isConnected = true) }
                requestSharedFlow.collect {
                    it.invoke(rSocket)
                }
            }
        }
    }

    fun nltkSearch(text: String) {
        viewModelScope.launch {
            requestSharedFlow.emit {
                try {
                    val response = it.requestResponse(
                        createSimplePayload("/nltk", text)
                    )
                    val wordSpeechParts = extractWordSpeechParts(response.data.readText())
                    _nltkStateFlow.update { wordSpeechParts }
                } catch (e: Throwable) {
                    _nltkErrorStateFlow.update { e }
                }
            }
        }
    }

    private fun extractWordSpeechParts(text: String): List<WordSpeechPart> {
        return Jackson.readValue(text, jacksonTypeRef<List<List<String>>>())
            .map { WordSpeechPart(it[0], it[1]) }
    }
}