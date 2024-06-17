package com.reverie.sdk.stt_batch

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.sdk.utilities.PermissionUtils
import com.reverie.sdk.utilities.constants.BATCH_RECALL_003
import com.reverie.sdk.utilities.constants.BATCH_RECALL_004
import com.reverie.sdk.utilities.constants.BATCH_SUCCESS
import com.reverie.sdk.utilities.constants.HEADER_API_KEY
import com.reverie.sdk.utilities.constants.HEADER_APPNAME
import com.reverie.sdk.utilities.constants.HEADER_APP_ID
import com.reverie.sdk.utilities.constants.HEADER_DOMAIN
import com.reverie.sdk.utilities.constants.REVERIE_BASE_URL_BATCH
import com.reverie.sdk.utilities.constants.REVERIE_BASE_URL_BATCH_STATUS
import com.reverie.sdk.utilities.constants.REVERIE_BASE_URL_BATCH_TRANSCRIPT
import com.reverie.sdk.utilities.constants.RevSdkConstants
import com.reverie.sdk.utilities.constants.STT_BATCH
import com.reverie.sdk.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.sdk.utilities.constants.WARNING_NO_INTERNET
import com.reverie.sdk.utilities.constants.WARNING_PERMISSIONS_GRANT_REQUIRED
import com.reverie.sdk.utilities.networking.Http
import com.reverie.sdk.utilities.networking.JSONObjectListener
//import kotlinx.serialization.json.Json
import org.json.JSONObject

/***
 * The Speech-to-Text API  powered by Reverie's AI technology accurately converts speech into text.  The solution can transcribe audio files of various Indian languages and audio formats.
 * The solution is a fully managed and continually trained solution, which leverages machine learning to combine knowledge of grammar, language structure, and the composition of audio and voice signals to accurately transcribe the audio file.
 * @param apiKey The valid REV-API-KEY.
 * @param appId The valid REV-APP-ID.
 * @param listener The callback listener for handling response.
 * @param context The activity context for checking permissions.
 * @see <a href="https://docs.reverieinc.com/speech-to-text-batch-api> Speech to text | Batch API</a>
 * @see <a href="https://docs.reverieinc.com/speech-to-text-batch-api/language-codes"> Supported Language Codes </a>
 */
class BatchSTT(
    private val apiKey: String,
    private val appId: String,
    private val listener: BatchSTTResultListener,
    private val context: Context
) {
    private var headers = mutableMapOf<String, Any>()
    private var bodyData = JSONObject()

    /**
     * Mention the audio sample rate and file format of the uploaded file.
     * format : By default, the format = 16k_int16. (WAV, Signed 16 bit, 16,000 or 16K Hz).
     * @see <a href="https://docs.reverieinc.com/speech-to-text-file-api/supporting-audio-format">Supporting Audio Format</a>
     */
    var format: String? = null

    init {
        headers[HEADER_API_KEY] = apiKey
        headers[HEADER_APP_ID] = appId
        headers[HEADER_APPNAME] = STT_BATCH
        headers["Content-Type"] = "application/json"
        if (format != null) headers["format"] = format!!

    }

    private val handler = Handler(Looper.getMainLooper())


    /**
     * Method for converting the audio file to text.
     * @param path The file path of the recorded audio.
     * @param domain The domain code,refer to Domain codes.
     * @param sourceLanguage The language in which the audio is spoken; refer to the supported languages.
     * @see <a href="https://docs.reverieinc.com/speech-to-text-file-api/language-codes">Supported Language Codes</a>
     * @see <a href="https://docs.reverieinc.com/speech-to-text-file-api#supporting-domain">Domain Codes</a>
     */

    fun uploadAudio(
        path: String,
        domain: String,
        sourceLanguage: String,
    ) {
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

        if (PermissionUtils.checkManifestPermissions(context, *requiredPermissions)) {
            if (PermissionUtils.isInternetAvailable(context)) {
                if (PermissionUtils.checkStoragePermissions(context)) {
                    headers[HEADER_DOMAIN] = domain
                    headers["src_lang"] = sourceLanguage
                    requestUpload(path)
                } else {
                    handler.post {
                        listener.onFailure(
                            BatchSTTErrorResponseData(
                                WARNING_PERMISSIONS_GRANT_REQUIRED + WARNING_PERMISSIONS_GRANT_REQUIRED,
                                0
                            )
                        )
                    }
                }
            } else {
                handler.post {
                    listener.onFailure(BatchSTTErrorResponseData(WARNING_NO_INTERNET, 0))
                }
            }
        } else {
            handler.post {
                handler.post {
                    listener.onFailure(
                        BatchSTTErrorResponseData(
                            WARNING_MISSING_MANIFEST + requiredPermissions.contentToString(),
                            0
                        )
                    )
                }
            }
        }

    }

    private fun requestUpload(path: String) {

        Http.Request(Http.POST)
            .header(headers)
            .body(bodyData)
            .url(REVERIE_BASE_URL_BATCH)
            .makeMultipartRequestBatch(object : JSONObjectListener {
                override fun onResponse(res: JSONObject?) {

                    //rename handleResponse
                    //callback response addition
                    uploadResponseHandling(res.toString())
                    if (RevSdkConstants.VERBOSE)
                        Log.d(
                            TAG,
                            "Upload:onResponse\n${res.toString()}"
                        )

                }

                //For general exceptions
                override fun onException(e: Exception?) {
                    Log.e(
                        TAG,
                        "Upload:onException\n${e.toString()}"
                    )

                    handler.post {
                        listener.onFailure(BatchSTTErrorResponseData(e.toString(), 0))
                    }
                }

                //For response related errors
                override fun onError(res: JSONObject?) {
                    Log.e(TAG, "Upload:onError\n${res.toString()}")

                    handler.post {
                        listener.onFailure(parseErrorJson(res.toString()))
                    }

                }
            }, path)


    }

    private fun parseErrorJson(jsonResponse: String): BatchSTTErrorResponseData {

        try {
            val jsonObject = JSONObject(jsonResponse)
            // Extract values from the JSON object
            val message = jsonObject.getString("message")
            val status = jsonObject.getInt("status")
            return BatchSTTErrorResponseData(message, status)
        } catch (e: Exception) {

            return BatchSTTErrorResponseData("Error Parsing json", 0)

        }
    }

    private fun uploadResponseHandling(jsonData: String) {

        try {
            val jsonObject = JSONObject(jsonData)
            val jobId = jsonObject.getString("job_id")
            val code = jsonObject.getString("code")
            val message = jsonObject.getString("message")

            if (code.equals(BATCH_SUCCESS)) {
                handler.post {
                    listener.onUploadSuccess(BatchUploadResponse(jobId, code, message))
                }

            } else {
                //Here we can return error which occurs when the code is not 000
                handler.post {
                    listener.onFailure(BatchSTTErrorResponseData(message, 0))
                }
            }
        } catch (e: Exception) {
            handler.post {
                listener.onFailure(BatchSTTErrorResponseData("Error Parsing json : $jsonData", 0))
            }
        }


    }

    /**
     * Method for checking the status of the uploaded file
     * @param jobId (String) Enter the job-id you received after uploading the file
     */
    fun checkStatus(jobId: String) {
        Http.Request(Http.GET)
            .header(headers)
            .url(REVERIE_BASE_URL_BATCH_STATUS + jobId)
            .makeRequest(object : JSONObjectListener {
                override fun onResponse(res: JSONObject?) {
                    if (res != null) {
                        statusResponseHandling(res.toString())
                        if (RevSdkConstants.VERBOSE)
                            Log.d(TAG, "checkStatus:onResponse\n$res")

                    }
                }

                //For general exceptions
                override fun onException(e: Exception?) {
                    Log.e(TAG, "checkStatus:onException\n${e.toString()}")

                    handler.post {
                        listener.onFailure(BatchSTTErrorResponseData(e.toString(), 0))
                    }

                }

                //For response related errors
                override fun onError(res: JSONObject?) {
                    Log.e(TAG, "checkStatus:onError\n${res.toString()}")

                    handler.post {
                        listener.onFailure(parseErrorJson(res.toString()))
                    }

                }
            })

    }


    private fun statusResponseHandling(jsonData: String) {

        try {
            val jsonObject = JSONObject(jsonData)
            val jobId = jsonObject.getString("job_id")
            val code = jsonObject.getString("code")
            val message = jsonObject.getString("message")
            val status = jsonObject.getString("status")
            if (code.equals(BATCH_RECALL_003)
                || code.equals(BATCH_RECALL_004)
                || code.equals(
                    BATCH_SUCCESS
                )
            ) {
                handler.post {
                    listener.onStatusSuccess(BatchStatusResponse(jobId, code, message, status))
                }
            } else
                handler.post {
                    listener.onFailure(BatchSTTErrorResponseData(message, 0))
                }

        } catch (e: Exception) {
            handler.post {
                listener.onFailure(BatchSTTErrorResponseData("Error parsing json $jsonData", 0))
            }


        }
    }

    /**
     * Method for getting the transcript of the uploaded file
     * @param jobId (String) Enter the jobId you received after uploading the file
     */
    fun getTranscript(jobId: String) {
        Http.Request(Http.GET)
            .header(headers)
            .url(REVERIE_BASE_URL_BATCH_TRANSCRIPT + jobId)
            .makeRequest(object : JSONObjectListener {
                override fun onResponse(res: JSONObject?) {
                    if (res != null) {
                        val responseItem = transcriptResponseHandling(res.toString())
                        if (RevSdkConstants.VERBOSE)
                            Log.d(
                                TAG,
                                "checkStatus:onResponse\n$res"
                            )
                        if (responseItem is BatchTranscriptResponse) {
                            handler.post {
                                listener.onTranscriptSuccess(responseItem)
                            }

                        } else if (responseItem is BatchSTTErrorResponseData) {
                            handler.post {
                                listener.onFailure(responseItem)
                            }
                        }

                    }
                }

                //For general exceptions
                override fun onException(e: Exception?) {
                    Log.e(TAG, "checkStatus:onException\n${e.toString()}")

                    handler.post {
                        listener.onFailure(BatchSTTErrorResponseData(e.toString(), 0))
                    }
                }

                //For response related errors
                override fun onError(res: JSONObject?) {
                    Log.e(TAG, "checkStatus:onError\n${res.toString()}")

                    handler.post {
                        listener.onFailure(parseErrorJson(res.toString()))
                    }

                }
            })

    }

    private fun transcriptResponseHandling(jsonData: String): Any {


        try {
            val jsonObject = JSONObject(jsonData)
            val code = jsonObject.getString("code")
            val message = jsonObject.getString("message")
            if (code.equals(BATCH_SUCCESS)) {

                val jobId = jsonObject.getString("job_id")
                val resultObject = jsonObject.getJSONObject("result")
                val transcript = resultObject.getString("transcript")
                val oringalTranscript = resultObject.getString("oringal_transcript")
                val channelNumber = resultObject.getInt("channel_number")

                val wordsArray = resultObject.getJSONArray("words")
                val words = mutableListOf<Word>()

                for (i in 0 until wordsArray.length()) {
                    val wordObject = wordsArray.getJSONObject(i)
                    val word = Word(
                        conf = wordObject.getDouble("conf"),
                        end = wordObject.getDouble("end"),
                        start = wordObject.getDouble("start"),
                        word = wordObject.getString("word")
                    )
                    words.add(word)
                }

                val result = Result(
                    transcript = transcript,
                    oringal_transcript = oringalTranscript,
                    channel_number = channelNumber,
                    words = words
                )

                return BatchTranscriptResponse(
                    job_id = jobId,
                    code = code,
                    message = message,
                    result = result
                )

            }
            return BatchSTTErrorResponseData(message, 0)
        } catch (e: Exception) {
            return BatchSTTErrorResponseData("Error parsing json $jsonData", 0)
        }
    }

    companion object {
        private const val TAG = "BatchSTT"
    }

}