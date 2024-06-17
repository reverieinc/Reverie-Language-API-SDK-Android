package com.reverie.sdk.stt_stream

interface StreamingSTTResultListener {

    fun onResult(result: StreamingSTTResultData?)
    fun onError(result: StreamingSTTErrorResponseData)


    fun onRecordingStart(status: Boolean)

    fun onRecordingEnd(status: Boolean)

    fun onRecordingData(data: ByteArray, amplitude: Int)


}