package com.reverie.sdk.stt_stream

data class StreamingSTTResultData(
    val id: String,
    val success: Boolean,
    val final: Boolean,
    val text: String,
    val cause: String,
    val confidence: Double,
    val display_text: String
)

data class StreamingSTTErrorResponseData(val error: String, val code: Int)