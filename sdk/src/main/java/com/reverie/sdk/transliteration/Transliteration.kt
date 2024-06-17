package com.reverie.sdk.transliteration

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.sdk.utilities.PermissionUtils
import com.reverie.sdk.utilities.constants.HEADER_API_KEY
import com.reverie.sdk.utilities.constants.HEADER_APPNAME
import com.reverie.sdk.utilities.constants.HEADER_APP_ID
import com.reverie.sdk.utilities.constants.HEADER_DOMAIN
import com.reverie.sdk.utilities.constants.HEADER_SOURCE_LANGUAGE
import com.reverie.sdk.utilities.constants.HEADER_TARGET_LANGUAGE
import com.reverie.sdk.utilities.constants.REVERIE_BASE_URL
import com.reverie.sdk.utilities.constants.RevSdkConstants
import com.reverie.sdk.utilities.constants.TRANSLITERATION_APP_NAME
import com.reverie.sdk.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.sdk.utilities.constants.WARNING_NO_INTERNET
import com.reverie.sdk.utilities.networking.Http
import com.reverie.sdk.utilities.networking.JSONObjectListener
import org.json.JSONArray
import org.json.JSONObject

private var headers = mutableMapOf<String, Any>()
private var bodyData = JSONObject()

/**
 * This class can be used to help users accurately transliterate their text using an API powered by Reverie's AI technology.
 * The solution will transliterate the text in real-time into various Indian languages.
 * @param apiKey The valid REV-API-KEY.
 * @param appId The valid REV-APP-ID.
 * @param domain Specify the domain code, e.g., Banking, Insurance, etc.
 * @param listener The callback listener for handling response.
 * @param context The activity context for checking permissions.
 * @see <a href="https://docs.reverieinc.com/reference/transliteration-api">Transliteration document</a>
 * @see<a href="https://docs.reverieinc.com/reference/transliteration-api/language-codes">Language Codes</a>
 *
 */

class Transliteration(
    apiKey: String,
    appId: String,
    domain: Int,
    val listener: TransliterationResultListener,
    var context: Context

) {
    private lateinit var sourceLanguage: String
    private lateinit var targetLanguage: String
    var handler = Handler(Looper.getMainLooper())

    /**
     * Specify whether the API should initially search in the Exception DB to transliterate the input text.
     * Note: By default, `isBulk` is set to true and will not search in the Exception DB.
     */

    var isBulk: Boolean? = null

    /**
     * Specify the number of transliteration suggestions the API should return for the input text.
     * Note: By default, `noOfSuggestions` is set to 1, meaning the API will return only one transliteration suggestion for the input string.
     */
    var noOfSuggestions: Int? = null

    /**
     * The `abbreviate` parameter validates whether any abbreviations/acronyms are passed in the input text and transliterates them accurately.
     * Note: By default, `abbreviate` is set to true.
     * Note: If the value is false, then the API will consider the abbreviation as a word and transliterate it to the nearest available word.
     * Note: In the input text, pass the abbreviations in uppercase.
     */

    var abbreviate: Boolean? = null

    /**
     * Specify whether to convert the numbers in the input text to the target language script.
     * Values can be one of the following: local, roman, words.
     * Note: By default, the `convertNumber` property is set to "roman".
     */
    // TODO: need to make this a string instead of boolean. Waiting for proper documentation updates 
    var convertNumber: String? = null

    /**
     * Specify whether you want to retain entities such as email IDs and URLs in the input text.
     * Note: By default, `ignoreTaggedEntities` is set to true, which means the API will transliterate email IDs and URLs into the target language script.
     */
    var ignoreTaggedEntities: Boolean? = null

    /**
     * This is used for transliterating Roman numbers to English numbers.
     * Note: Default value is false.
     * For example, if the user types "sector V" in English, the transliteration would be "सेक्टर 5" in Hindi.
     * "Block II" will be transliterated as "ब्लॉक 2".
     * Note: To translate numbers into Indian languages, use the `convertNumber` parameter as mentioned in the table.
     */
    var convertRoman: Boolean? = null

    /**
     * This is used for transliterating ordinal values to English numbers.
     * Note: Default value is false.
     * For example, if the user types "15th Main" in English, the transliteration would be "15 मेन" in Hindi.
     */
    var convertOrdinal: Boolean? = null

    /**
     * This is used to produce the abbreviation output without a dot.
     * Note: Default value is false.
     * For example, if a user wants an abbreviation output without a dot and is given "SMS" as an input, then the result would be "एसएमएस".
     */
    var abbreviationWithoutDot: Boolean? = null

    /**
     * The language of the words in the input text.
     *
     * Example -
     *
     * “data”: “Singh Sahab aap Kahan the.”
     *
     * In the example above, the Hindi language words are written in the English language script (Roman Script). So cnt_lang = “hi”
     *
     * This is an optional parameter. If no value is provided, by default the value is the same as src_lang.
     */
    var cnt_lang: String? = null


    init {
        headers["Content-Type"] = "application/json"
        headers[HEADER_API_KEY] = apiKey
        headers[HEADER_APP_ID] = appId
        headers[HEADER_APPNAME] = TRANSLITERATION_APP_NAME
        headers[HEADER_DOMAIN] = domain
        if (cnt_lang != null) headers["cnt_lang"] = cnt_lang!!
    }

    /**
     * Method for translating text.
     * @param data The list of text to be transliterated.
     * @param sourceLanguage The language code of the input text.
     * @param targetLanguage The language code of the desired transliteration.
     */
    fun transliterate(
        data: List<String>,
        sourceLanguage: String,
        targetLanguage: String,

        ) {
        this.sourceLanguage = sourceLanguage
        this.targetLanguage = targetLanguage
        headers[HEADER_SOURCE_LANGUAGE] = sourceLanguage
        headers[HEADER_TARGET_LANGUAGE] = targetLanguage
        val dataArray = JSONArray(data)
        bodyData = JSONObject().apply {
            put("data", dataArray)
            if (isBulk != null) put("isBulk", isBulk)
            if (noOfSuggestions != null) put("noOfSuggestions", noOfSuggestions)
            if (abbreviate != null) put("abbreviate", abbreviate)
            if (convertNumber != null) put("convertNumber", convertNumber)
            if (ignoreTaggedEntities != null) put("ignoreTaggedEntities", ignoreTaggedEntities)
            if (convertRoman != null) put("convertRoman", convertRoman)
            if (convertOrdinal != null) put("convertOrdinal", convertOrdinal)
            if (abbreviationWithoutDot != null) put(
                "abbreviationWithoutDot",
                abbreviationWithoutDot
            )


        }
        if (RevSdkConstants.VERBOSE)
            Log.d(TAG, "transliterate: bodyData\n$bodyData")
        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        if (PermissionUtils.checkManifestPermissions(context, *requiredPermissions)) {
            if (PermissionUtils.isInternetAvailable(context)) {
                request()
            } else {
                handler.post {
                    listener.onFailure(TransliterationError(WARNING_NO_INTERNET, 0))
                }
            }
        } else {
            handler.post {
                listener.onFailure(
                    TransliterationError(
                        WARNING_MISSING_MANIFEST + requiredPermissions.contentToString(),
                        0
                    )
                )
            }
        }


    }


    private fun parseJson(jsonData: String): TransliterationData {
        val jsonObject = JSONObject(jsonData)

        val responseListJson = jsonObject.getJSONArray("responseList")
        val responseList = mutableListOf<ItemTransliteration>()

        for (i in 0 until responseListJson.length()) {
            val responseItemJson = responseListJson.getJSONObject(i)
            val inString = responseItemJson.getString("inString")
            val outStringArray = responseItemJson.getJSONArray("outString")
            val outString = outStringArray.getString(0)
            val apiStatus = responseItemJson.getInt("apiStatus")

            val responseItem = ItemTransliteration(inString, outString, apiStatus)
            responseList.add(responseItem)
        }

        return TransliterationData(responseList)
    }

    private fun parseErrorJson(jsonResponse: String): TransliterationError {
      try {


          val jsonObject = JSONObject(jsonResponse)

          val message = jsonObject.getString("message")
          val status = jsonObject.getInt("status")

          return TransliterationError(message, status)
      }
      catch (e :Exception)
      {
          return  TransliterationError("Error parsing json $jsonResponse",0)

      }
    }

    private fun request() {
        Http.Request(Http.POST)
            .header(headers)
            .body(bodyData)
            .url(REVERIE_BASE_URL)
            .makeRequest(object : JSONObjectListener {
                override fun onResponse(res: JSONObject?) {
                    if (res != null) {
//                        if (RevSdkConstants.VERBOSE) {
                        if (RevSdkConstants.VERBOSE) {
                            Log.d(TAG, "request:onResponse\n${res.toString()}")
                        }

                        try{
                        val response = parseJson(res.toString())
                        handler.post {
                            listener.onSuccess(response)
                        }}
                        catch (e:Exception)
                        {

                            listener.onFailure(TransliterationError("Error parsing json ${res.toString()}", 0))

                        }
                    }
                }


                override fun onException(e: Exception?) {
                    Log.e(TAG, "request:onException\n${e.toString()}")
                    handler.post {
                        listener.onFailure(TransliterationError(e.toString(), 0))
                    }
                }

                override fun onError(res: JSONObject?) {
                    Log.e(TAG, "request:onError\n${res.toString()}")
                    if (res != null) {
                        handler.post {
                            listener.onFailure(parseErrorJson(res.toString()))
                        }
                    }
                }
            })


    }

    companion object {
        private const val TAG = "Transliteration"
    }


}













