package com.reverie.sdk.stt_file

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.sdk.utilities.PermissionUtils
import com.reverie.sdk.utilities.constants.HEADER_API_KEY
import com.reverie.sdk.utilities.constants.HEADER_APPNAME
import com.reverie.sdk.utilities.constants.HEADER_APP_ID
import com.reverie.sdk.utilities.constants.HEADER_DOMAIN
import com.reverie.sdk.utilities.constants.REVERIE_BASE_URL
import com.reverie.sdk.utilities.constants.RevSdkConstants
import com.reverie.sdk.utilities.constants.STT_APP_NAME
import com.reverie.sdk.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.sdk.utilities.constants.WARNING_NO_INTERNET
import com.reverie.sdk.utilities.constants.WARNING_PERMISSIONS_GRANT_REQUIRED
import com.reverie.sdk.utilities.networking.Http
import com.reverie.sdk.utilities.networking.JSONObjectListener
import org.json.JSONObject


/**
 * This class can be used to help user accurately convert their speech into text using an API powered by Reverie's AI technology. The solution will transcribe the speech in real-time of various Indian languages and audio formats.

 * @param apiKey The valid REV-API-KEY.
 * @param appId The valid REV-APP-ID.
 * @param listener The callback listener for handling response.
 * @param context The activity context for checking permissions.
 * @see <a href="https://docs.reverieinc.com/speech-to-text-file-api">Speech to Text | File API</a>
 *
 */

class FileSTT(
    private var apiKey: String,
    private var appId: String,
    private var listener: FileSTTResultListener,
    private var context: Context
) {
    private val handler = Handler(Looper.getMainLooper())


    /**
     * Default value=true
     * Possible values are :
     * 1. true - stores client’s audio and keeps transcript in logs.
     * 2. no_audio -  does not store client’s audio but keeps transcript in logs.
     * 3. no_transcript - does not keep transcript in logs but stores client’s audio.
     * 4. false - does not keep neither client’s audio nor transcript in log.
     */
    var logging: String? = null

    /**
     * Mention the audio sample rate and file format of the uploaded file.
     * format : By default, the format = 16k_int16. (WAV, Signed 16 bit, 16,000 or 16K Hz).
     * @see <a href="https://docs.reverieinc.com/speech-to-text-file-api/supporting-audio-format">Supporting Audio Format</a>
     */
    var format: String? = null

    /**
     * Method for converting the audio file to text
     * @param path Audio File Path (where it is saved after recording),
     * @param domain (String) Specify the domain code.
     * @param sourceLanguage (String) Indicates the language in which the audio is spoken; refer to supported language
     * @see <a href="https://docs.reverieinc.com/speech-to-text-file-api/language-codes">Supported Language Codes</a>
     */
    fun audioToText(
        path: String,
        domain: String,
        sourceLanguage: String,
    ) {
        if (logging != null) headers["logging"] = logging!!
        if (format != null) headers["format"] = format!!
        // TODO: make an option for URI as an file path alternative
        // TODO: Plan is to support cloud files too (AWS, Gdrive etc)

        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        // TODO: send the manifest permissions
        val requiredReadPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(

                "Provide Permission to read file and Audio"
            )
        } else {
            arrayOf(

                "Provide Permission to read file and Audio"
            )
        }

        if (PermissionUtils.checkManifestPermissions(context, *requiredPermissions)) {
            if (PermissionUtils.isInternetAvailable(context)) {
                if (PermissionUtils.checkStoragePermissions(context)) {
                    headers[HEADER_DOMAIN] = domain
                    headers["src_lang"] = sourceLanguage
                    request(path)
                } else {
                    handler.post {
                        listener.onFailure(
                            FileSTTErrorResponseData(
                                WARNING_PERMISSIONS_GRANT_REQUIRED + requiredReadPermissions.contentToString(),
                                0
                            )
                        )
                    }
                }
            } else {
                handler.post {
                    listener.onFailure(FileSTTErrorResponseData(WARNING_NO_INTERNET, 0))
                }
            }
        } else {
            handler.post {
                handler.post {
                    listener.onFailure(
                        FileSTTErrorResponseData(
                            WARNING_MISSING_MANIFEST + requiredPermissions.contentToString(),
                            0
                        )
                    )
                }
            }
        }

    }

    private fun parseErrorJson(jsonResponse: String): FileSTTErrorResponseData {

       try{

        val jsonObject = JSONObject(jsonResponse)

        // Extract values from the JSON object
        val message = jsonObject.getString("message")
        val status = jsonObject.getInt("status")

        return FileSTTErrorResponseData(message, status)}
       catch (e:Exception)
       {
           return FileSTTErrorResponseData("Error parsing json :$jsonResponse", 0)
       }

    }

    private fun parseJson(jsonData: String): Any {

        try{

        val jsonObject = JSONObject(jsonData)

        val id = jsonObject.getString("id")
        val success = jsonObject.getBoolean("success")
        val text = jsonObject.getString("text")
        val final = jsonObject.getBoolean("final")
        val displayText = jsonObject.getString("display_text")
        val cause = jsonObject.getString("cause")
        val confidence = jsonObject.getInt("confidence")

        if (!success) {
            return FileSTTErrorResponseData(cause, confidence)
        }

        return FileSTTResultData(displayText, success = true, cause, id, confidence, final, text)}

        catch (e:Exception)
        {

            return FileSTTErrorResponseData("Error parsing json $jsonData", 0)
        }
    }

    private var headers = mutableMapOf<String, Any>()
    private var bodyData = JSONObject()


    init {
        headers[HEADER_API_KEY] = apiKey
        headers[HEADER_APP_ID] = appId
        headers[HEADER_APPNAME] = STT_APP_NAME
        headers["Content-Type"] = "application/json"

    }

    private fun request(path: String) {
        Http.Request(Http.POST)
            .header(headers)
            .body(bodyData)
            .url(REVERIE_BASE_URL)
            .makeMultipartRequest(object : JSONObjectListener {
                override fun onResponse(res: JSONObject?) {
                    if (res != null) {
                        if (RevSdkConstants.VERBOSE)
                            Log.d(TAG, "request:onResponse\n${res}")
                        val responseItem = parseJson(res.toString())
                        if (responseItem is FileSTTResultData) {

                            handler.post {
                                listener.onSttSuccess(responseItem)
                            }
                        } else if (responseItem is FileSTTErrorResponseData) {
                            handler.post {
                                listener.onFailure(responseItem)
                            }
                        }
                    }
                }

                //For general exceptions
                override fun onException(e: Exception?) {
                    Log.e(TAG, "request:onException\n${e.toString()}")

                    handler.post {
                        listener.onFailure(FileSTTErrorResponseData(e.toString(), 0))
                    }
                }

                //For response related errors
                override fun onError(res: JSONObject?) {
                    Log.e(TAG, "request:onError\n${res.toString()}")

                    handler.post {
                        listener.onFailure(parseErrorJson(res.toString()))
                    }

                }
            }, path)

    }

    companion object {
        private const val TAG = "FileSTT"
    }

}



