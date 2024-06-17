package com.reverie.sdk.translation

interface TranslationResultListener {

    fun onSuccess(response: TranslationData)
    fun onFailure(error: TranslationError)

}