package com.simenko.qmapp.data.remote.serializer

import kotlinx.serialization.json.Json

object JsonSingleton {
    val networkJson = Json { ignoreUnknownKeys = true }
}