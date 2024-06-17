package com.reverie.sdk.tts

import java.io.File

data class TTSAudioData(val wavfile: File)
data class TTSErrorResponse(val message: String, val errorCode: Int)
