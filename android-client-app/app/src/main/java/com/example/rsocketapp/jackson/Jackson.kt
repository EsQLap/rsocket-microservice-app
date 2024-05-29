package com.example.rsocketapp.jackson

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

object Jackson : ObjectMapper() {

    init {
        findAndRegisterModules()
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}