package com.simenko.qmapp.retrofit.entities

data class NetworkErrorBody(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val exception: String?
)