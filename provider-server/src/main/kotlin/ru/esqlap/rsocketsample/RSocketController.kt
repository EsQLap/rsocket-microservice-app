package ru.esqlap.rsocketsample

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class RSocketController(
    private val rSocketRequester: RSocketRequester,
) {

    @MessageMapping("/nltk")
    fun nltk(requestMono: Mono<String>): Mono<List<List<String>>> {
        return requestMono
            .flatMap {
                rSocketRequester
                    .route("")
                    .data(it)
                    .retrieveMono(typeReference<List<List<String>>>())
            }
    }
}