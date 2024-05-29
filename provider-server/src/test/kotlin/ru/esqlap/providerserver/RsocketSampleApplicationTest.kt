package ru.esqlap.providerserver

import com.google.common.truth.Truth
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.test.StepVerifier


@SpringBootTest
class RsocketSampleApplicationTest {

    @Test
    fun testNltkServer() {
        val request =
            "Анна Павловна Шерер, напротив, несмотря на свои сорок лет, была преисполнена оживления и порывов."
        val result = requester.route("/nltk")
            .data(request)
            .retrieveMono(typeReference<List<List<String>>>())
        StepVerifier
            .create(result)
            .assertNext {
                println(it)
                Truth.assertThat(it).isNotEmpty()
            }
            .verifyComplete()
    }

    companion object {

        lateinit var requester: RSocketRequester

        @JvmStatic
        @BeforeAll
        fun setupConnection(
            @Autowired builder: RSocketRequester.Builder,
            @Value("\${spring.rsocket.server.port}") port: Int,
        ) {
            requester = builder.tcp("localhost", port)
        }

        @JvmStatic
        @AfterAll
        fun disposeConnection() {
            requester.dispose()
        }
    }
}
