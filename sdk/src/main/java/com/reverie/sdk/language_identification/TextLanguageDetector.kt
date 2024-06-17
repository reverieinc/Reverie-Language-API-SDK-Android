package com.reverie.sdk.language_identification

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.sdk.utilities.PermissionUtils
import com.reverie.sdk.utilities.constants.HEADER_API_KEY
import com.reverie.sdk.utilities.constants.HEADER_APPNAME
import com.reverie.sdk.utilities.constants.HEADER_APP_ID
import com.reverie.sdk.utilities.constants.LANGUGE_IDENTIFICATION
import com.reverie.sdk.utilities.constants.REVERIE_BASE_URL
import com.reverie.sdk.utilities.constants.RevSdkConstants
import com.reverie.sdk.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.sdk.utilities.constants.WARNING_NO_INTERNET
import com.reverie.sdk.utilities.networking.Http
import com.reverie.sdk.utilities.networking.JSONObjectListener
import org.json.JSONObject

/**
 * This class can be used to detect or identify the language of the source content.
 * @param apiKey The valid REV-API-KEY.
 * @param appId The valid REV-APP-ID.
 * @param context The activity context for checking permissions.
 * @param listener The callback listener for handling response.
 * @see <a href="https://docs.reverieinc.com/language-identification-api">Language Identification API</a>
 *
 */
class TextLanguageDetector
    (
    private val apiKey: String,
    private val appId: String,
    val context: Context,
    val listener: TextLanguageDetectionListener
) {
    private var headers = mutableMapOf<String, Any>()
    private var bodyData = JSONObject()

    /**
     * (optional)
     * Length of the string to be processed for tokenization.
     * It should be a number in the power of 2(i.e, 16 = 2^4, 32 = 2^5 ..).
     * Max value could be 512.
     */
    var maxlength: Int? = null
    private val handler = Handler(Looper.getMainLooper())

    init {

        headers[HEADER_API_KEY] = apiKey
        headers[HEADER_APP_ID] = appId
        headers[HEADER_APPNAME] = LANGUGE_IDENTIFICATION
        headers["Content-Type"] = "application/json"


    }

    /**
     * Call this method to detect or identify the language of the input text
     * @param text The input text for language detection.
     */
    fun identifyLanguage(text: String) {
        bodyData = JSONObject().apply {
            put("text", text)
            if (maxlength != null) put("max_length", maxlength)
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
                    listener.onFailure(TextLanguageDetectionError(WARNING_NO_INTERNET))
                }
            }
        } else {

            handler.post {
                listener.onFailure(
                    TextLanguageDetectionError(
                        WARNING_MISSING_MANIFEST + requiredPermissions.contentToString()
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
                            Log.d(TAG, "request:onResponse\n${res}")

                        try {


                            val responseBody = parseJson(res.toString())
                            handler.post {
                                listener.onSuccess(responseBody)
                            }
                        } catch (e: Exception)
                        {

                            listener.onFailure(TextLanguageDetectionError("Error parsing response : ${res.toString()}"))
                        }
                    }
                }

                //For general exceptions
                override fun onException(e: Exception?) {
                    Log.e(TAG, "request:onException\n${e.toString()}")
                    handler.post {
                        listener.onFailure(TextLanguageDetectionError(e.toString()))
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

    private fun parseJson(jsonData: String): TextLanguageDetectionResult {
        val jsonObject = JSONObject(jsonData)
        val lang = jsonObject.getString("lang")
        val confidence = jsonObject.getDouble("confidence")
        return TextLanguageDetectionResult(lang, confidence)
    }

    private fun parseErrorJson(jsonData: String): TextLanguageDetectionError {
        return TextLanguageDetectionError(jsonData)
    }

    companion object {
        private const val TAG = "TextLanguageDetector"
    }
}