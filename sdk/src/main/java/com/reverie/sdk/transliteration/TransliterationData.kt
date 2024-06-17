package com.reverie.sdk.transliteration


data class ItemTransliteration(val inString: String, val outString: String, val apiStatus: Int)

data class TransliterationData(val responseList: List<ItemTransliteration>)

data class TransliterationError(val message: String, val errorCode: Int)