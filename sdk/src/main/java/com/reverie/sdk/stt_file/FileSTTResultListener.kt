package com.reverie.sdk.stt_file

interface FileSTTResultListener {
    fun onSttSuccess(response: FileSTTResultData)
    fun onFailure(error: FileSTTErrorResponseData)
}