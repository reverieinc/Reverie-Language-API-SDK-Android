package com.reverie.sdk.translation


data class ResponseItem(val inString: String, val outString: String, val apiStatus: Int)

data class TranslationData(val responseList: List<ResponseItem>, val tokenConsumed: Int)

data class TranslationError(val message: String, val errorCode: Int)

