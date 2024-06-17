package com.reverie.sdk.stt_batch

data class BatchSTTResultData(
    val transcript: String,
    val words: MutableList<Words>
)

data class Word(
    val conf: Double,
    val end: Double,
    val start: Double,
    val word: String
)


data class Result(
    val transcript: String,
    val oringal_transcript: String,
    val channel_number: Int,
    val words: List<Word>
)


data class BatchTranscriptResponse(
    val job_id: String,
    val code: String,
    val message: String,
    val result: Result
)


data class BatchSTTErrorResponseData(val message: String, val errorCode: Int)

data class BatchUploadResponse(
    val jobid: String,
    val code: String,
    val message: String
    // TODO: add all responses of Status API
)

data class Words(
    val conf: Double,
    val end: Double,
    val start: Double,
    val word: String
)

data class BatchStatusResponse(
    val jobid: String,
    val code: String,
    val message: String,
    val status: String
)