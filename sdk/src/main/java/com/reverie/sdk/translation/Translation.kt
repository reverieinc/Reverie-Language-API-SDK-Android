package com.reverie.sdk.translation


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
import com.reverie.sdk.utilities.constants.TRANSLATION_APP_NAME
import com.reverie.sdk.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.sdk.utilities.constants.WARNING_NO_INTERNET
import com.reverie.sdk.utilities.networking.Http
import com.reverie.sdk.utilities.networking.JSONObjectListener
import org.json.JSONArray
import org.json.JSONObject

private var headers = mutableMapOf<String, Any>()
private var bodyData = JSONObject()


/**
 * This class can be used to help user accurately convert their text into translated text using an API powered by Reverie's AI technology. The solution will translate the text in real-time of various Indian languages

 * @param apiKey The valid REV-API-KEY.
 * @param appId The valid REV-APP-ID.
 * @param context The activity context for checking permissions.
 * @param domain Specify the domain code, e.g., Banking, Insurance, etc.
 * @param resultListener The callback listener for handling response.
 * @see <a href="https://docs.reverieinc.com/reference/translation-api">Translation document</a>
 * @see<a href="https://docs.reverieinc.com/reference/translation-api/language-codes">Language Codes</a>
 *
 */
class Translation(
    apiKey: String,
    appId: String,
    var context: Context,
    domain: Int,
    resultListener: TranslationResultListener
) {

    private var listener: TranslationResultListener
    private lateinit var sourceLang: String
    var handler = Handler(Looper.getMainLooper())

    /**
     * The feature to screen the non-dictionary words used in a sentence. In other words, the mask will indicate the words that should not be translated into the target language.
     * Note - By default, the nmtMask = false
     * Note - To set the nmtMask = true, it is mandatory the src_lang = en (English).
     */
    var nmtMask: Boolean? = null

    /**
     * The parameter will specify whether the application should refer to the Lookup DB or not.
     * i.e., when thedbLookupParamis True, the system will initially refer to the Database to fetch contents. Note By default, the dbLookupParam= false.
     */
    var dbLookUpParam: Boolean? = null

    /**
     * The parameter will specify whether the content should be segmented before localizing the input.
     * Note By default, the segmentationParam= false.
     */
    var segmentationParam: Boolean? = null

    /**
     * Specify whether the content localization process should use NMT technology or not.
     * i.e., When the nmtParam value is True, the system will initially refer to the Lookup database to localize content.
     * If the content is not available in the database, then the NMT is used for translation.
     * Note By default, the nmtParam= false
     */
    var nmtParam: Boolean? = null

    /**
     * Specify whether you want to pre-process the input and tag the strings according to patterns in the regex_pattern table Note By default, the builtInPreProc= false
     */
    var builtInPreProc: Boolean? = null

    /**
     *The Debug parameter will provide log details about localized content.
     * The details provided are the entity code, localization process type, and more.
     * This information is useful to capture the log and analyze the system performance.
     * Note By default, the debugMode= false
     */
    var debugMode: Boolean? = null

    /**
     *Specify whether you want to verify the unmoderated strings. Set usePrabandhak= true, then the API will send the unverified strings to the Prabandhak application for verification.
     * Note By default, the usePrabandhak= false.
     * Note - The usePrabandhak parameter is enabled only for the Reverie’s Manual Verification Service subscribers.
     */
    var usePrabandhak: Boolean? = null

    /**
     * Determines the Words that are to be masked.
     * Note - On defining values in the nmtMaskTerms, then automatically the nmtMask is set to true.
     * Example -
     * Masking a term -
     * "nmtMaskTerms": ["Reverie Language Technologies"]
     * Here, the API will mask the term Reverie Language Technologies, if found in the source content, and will transliterate the word.
     */
    var nmtMaskTerms: List<String> = listOf()


    /**
     * Determines the Words that are to be masked.
     * Note - On defining values in the nmtMaskTerms, then automatically the nmtMask is set to true.
     * Example -
     * Masking a term - "nmtMaskTerms": ["Reverie Language Technologies"]
     * Here, the API will mask the term Reverie Language Technologies, if found in the source content, and will transliterate the word.
     */
    fun setNmtMaskTerms(vararg newStrings: String) {
        nmtMaskTerms = newStrings.toList()
    }

    init {
        headers[HEADER_API_KEY] = apiKey
        headers[HEADER_APP_ID] = appId
        headers[HEADER_APPNAME] = TRANSLATION_APP_NAME
        headers["Content-Type"] = "application/json"
        headers[HEADER_DOMAIN] = domain
        listener = resultListener
    }

    /**
     * Method for translating text
     * @param data The list of text to be translated
     * @param sourceLanguage The language code of the input text.
     * @param targetLanguage The language code of the desired translation
     */
    fun translate(
        data: List<String>,
        sourceLanguage: String,
        targetLanguage: String
    ) {

        this.sourceLang = sourceLanguage
        val dataArray = JSONArray(data)
        headers[HEADER_SOURCE_LANGUAGE] = sourceLanguage
        headers[HEADER_TARGET_LANGUAGE] = targetLanguage
        bodyData = JSONObject().apply {
            put("data", dataArray)
            if (nmtMask != null) put("nmtParam", nmtMask)
            if (nmtMaskTerms.isNotEmpty()) put("nmtMaskTerms", JSONArray(nmtMaskTerms))
            if (usePrabandhak != null) put("usePrabandhak", usePrabandhak)
            if (debugMode != null) put("debugMode", debugMode)
            if (nmtParam != null) put("nmtParam", nmtParam)
            if (builtInPreProc != null) put("builtInPreProc", builtInPreProc)
            if (dbLookUpParam != null) put("dbLookUpParam", dbLookUpParam)
            if (segmentationParam != null) put("segmentatioParam", segmentationParam)
        }
        if (RevSdkConstants.VERBOSE) {
            Log.d(TAG, "translate body sent:$bodyData")
        }

        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        if (PermissionUtils.checkManifestPermissions(context, *requiredPermissions)) {
            if (PermissionUtils.isInternetAvailable(context)) {
                request()
            } else {
                handler.post {
                    listener.onFailure(TranslationError(WARNING_NO_INTERNET, 0))
                }
            }
        } else {

            handler.post {
                listener.onFailure(
                    TranslationError(
                        WARNING_MISSING_MANIFEST + requiredPermissions.contentToString(),
                        0
                    )
                )
            }

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
                        if (RevSdkConstants.VERBOSE)
                            Log.d(TAG, "request:onResponse\n${res.toString()}")
                       try{
                        val responseBody = parseJson(res.toString())
                        handler.post {
                            listener.onSuccess(responseBody)
                        }}
                       catch (e:Exception)
                       {
                           listener.onFailure(TranslationError("Error parsing json ${res.toString()}", 0))

                       }
                    }
                }

                //For general exceptions
                override fun onException(e: Exception?) {
                    Log.e(TAG, "request:onException\n${e.toString()}")
                    handler.post {
                        listener.onFailure(TranslationError(e.toString(), 0))
                    }
                }

                //For response related errors
                override fun onError(res: JSONObject?) {
                    Log.e(TAG, "request:onError\n${res.toString()}")
                    handler.post {
                        listener.onFailure(parseErrorJson(res.toString()))
                    }

                }
            })

    }


    private fun parseErrorJson(jsonResponse: String): TranslationError {
        try {

            val jsonObject = JSONObject(jsonResponse)

            val message = jsonObject.getString("message")
            val status = jsonObject.getInt("status")

            return TranslationError(message, status)
        } catch (e: Exception) {
            return TranslationError("Error parsing json $jsonResponse", 0)

        }
    }


    private fun parseJson(jsonData: String): TranslationData {




        val jsonObject = JSONObject(jsonData)
        val tokenConsumed = jsonObject.getInt("tokenConsumed")

        val responseListJson = jsonObject.getJSONArray("responseList")
        val responseList = mutableListOf<ResponseItem>()

        for (i in 0 until responseListJson.length()) {
            val responseItemJson = responseListJson.getJSONObject(i)
            val inString = responseItemJson.getString("inString")
            val outString = responseItemJson.getString("outString")
            val apiStatus = responseItemJson.getInt("apiStatus")

            val responseItem = ResponseItem(inString, outString, apiStatus)
            responseList.add(responseItem)
        }

        return TranslationData(responseList, tokenConsumed)


    }

    companion object {
        private const val TAG = "Translation"
    }

}