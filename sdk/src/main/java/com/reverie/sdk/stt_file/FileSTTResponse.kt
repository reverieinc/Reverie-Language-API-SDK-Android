package com.reverie.sdk.stt_file

data class FileSTTResultData(
    val display_text: String,
    val success: Boolean,
    val cause: String,
    val id: String,
    val confidence: Int,
    val final: Boolean,
    val text: String
)

data class FileSTTErrorResponseData(val message: String, val errorCode: Int)

