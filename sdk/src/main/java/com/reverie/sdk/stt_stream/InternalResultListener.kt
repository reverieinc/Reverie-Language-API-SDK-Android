package com.reverie.sdk.stt_stream

internal interface InternalResultListener {
    fun onResult(result: StreamingSTTResultData?)
    fun onError(result: StreamingSTTErrorResponseData)
}