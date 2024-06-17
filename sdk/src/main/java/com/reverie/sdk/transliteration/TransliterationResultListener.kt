package com.reverie.sdk.transliteration

interface TransliterationResultListener {

    fun onSuccess(response: TransliterationData)
    fun onFailure(error: TransliterationError)

}