package com.reverie.sdk.tts

interface TextToSpeechResultListener {
    fun onSuccess(file: TTSAudioData)
    fun onFailure(error: TTSErrorResponse)
    fun onError(res: TTSErrorResponse)
}