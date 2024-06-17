package com.reverie.sdk.stt_batch

interface BatchSTTResultListener {
    fun onTranscriptSuccess(response: BatchTranscriptResponse)
    fun onStatusSuccess(status: BatchStatusResponse)
    fun onUploadSuccess(response: BatchUploadResponse)
    fun onFailure(error: BatchSTTErrorResponseData)

}