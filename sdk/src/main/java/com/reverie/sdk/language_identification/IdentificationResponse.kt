package com.reverie.sdk.language_identification

data class TextLanguageDetectionResult(
    val language:String,
    val confidence:Double
)

data class TextLanguageDetectionError(
    val response:String
)