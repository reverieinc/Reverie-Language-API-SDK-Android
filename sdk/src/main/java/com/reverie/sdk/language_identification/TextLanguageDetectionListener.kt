package com.reverie.sdk.language_identification

interface TextLanguageDetectionListener {
    fun onSuccess(response: TextLanguageDetectionResult)
    fun onFailure(error:TextLanguageDetectionError)
}