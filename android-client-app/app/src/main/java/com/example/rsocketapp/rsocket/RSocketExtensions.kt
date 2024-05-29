package com.example.rsocketapp.rsocket

import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.compositeMetadata
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data

@OptIn(ExperimentalMetadataApi::class)
fun createSimplePayload(route: String, data: String) = buildPayload {
    compositeMetadata {
        add(RoutingMetadata(route))
    }
    data(data)
}
