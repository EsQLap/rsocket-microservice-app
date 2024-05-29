package ru.esqlap.providerserver

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.util.MimeTypeUtils
import reactor.util.retry.Retry
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Configuration
class RsocketClientConfiguration(
    @Value("\${nltk.uri}") private val nltkServerUri: String,
    @Value("\${nltk.port}") private val nltkServerPort: Int,
) {

    @Bean
    fun getRSocketRequester(): RSocketRequester {
        val builder = RSocketRequester.builder()

        return builder
            .rsocketConnector { rSocketConnector ->
                rSocketConnector.reconnect(Retry.fixedDelay(2, 2.seconds.toJavaDuration()))
            }
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
            .rsocketStrategies {
                it.encoder(Jackson2JsonEncoder())
                it.decoder(Jackson2JsonDecoder())
            }
            .tcp(nltkServerUri, nltkServerPort)
    }
}