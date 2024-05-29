package ru.esqlap.providerserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RsocketSampleApplication

fun main(args: Array<String>) {
    runApplication<RsocketSampleApplication>(*args)
}
