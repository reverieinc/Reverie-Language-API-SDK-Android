package com.reverie.sdk.tts

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.sdk.utilities.PermissionUtils
import com.reverie.sdk.utilities.constants.*
import com.reverie.sdk.utilities.networking.ByteArrayListener
import com.reverie.sdk.utilities.networking.Http
import org.json.JSONObject
import java.io.File


private var headers = mutableMapOf<String, Any>()
private var bodyData = JSONObject()

/**
 * This class can be used to help user turns text into lifelike speech, allowing you to create applications that talk in multiple Indic languages and build comprehensive speech-enabled products using an API powered by Reverie's AI technology.
 * @param apiKey The valid REV-API-KEY.
 * @param appId The valid REV-APP-ID.
 * @param context The activity context for checking permissions.
 * @param listener The callback listener for handling response.
 * @see <a href="https://docs.reverieinc.com/reference/text-to-speech-api/setup">Text to Speech setup</a>
 *
 */
class TextToSpeech(
    apiKey: String,
    appId: String,
    val context: Context,
    val listener: TextToSpeechResultListener,
) {
    var mContext = context
    var handler = Handler(Looper.getMainLooper())

    /**
     * Speaking pitch, in the range of -3 to 3.
     * Values:
     * - 3 indicates a decrease of 3 semitones from the original pitch.
     * 0 indicates no change from the original pitch.
     * +3 indicates an increase of 3 semitones from the original pitch.
     *
     * Note: By default, the pitch is set to 0.
     */

    var pitch: Float? = null

    /**
     * The speech rate of the audio file, allowing values from 0.5 (slowest speed rate) up to 1.5 (fastest speed rate).
     * Note: By default, the speed is set to 1 (normal speed).
     */
    var speed: Float? = null

    /**
     * The speech audio format to generate the audio file.
     * By default, the format = WAV
     * @see<a href="https://docs.reverieinc.com/reference/text-to-speech-api/supporting-audio-format">Supporting Audio Format</a>
     */
    var format: String? = null

    /**
     * The sampling rate (in hertz) to synthesize the audio output
     *  By default, the sample_rate = 22050 Hz (22.05kHz)
     */
    var sampleRate: Int? = null

    init {
        headers[HEADER_API_KEY] = apiKey
        headers[HEADER_APP_ID] = appId
        headers[HEADER_APPNAME] = TTS_APP_NAME
        headers["Content-Type"] = "application/json"
    }


    /**
     * Call this method to create the audio.
     * @param data Entered text in string format.
     * @param speaker Language code and voice type (female or male).
     * @see<a href="https://docs.reverieinc.com/reference/text-to-speech-api/supporting-speaker-code">Speaker Codes</a>
     *
     */
    fun speak(
        data: String,
        speaker: String,
    ) {
        headers[HEADER_SPEAKER] = speaker
        bodyData = JSONObject().apply {
            put("text", data)
            if (pitch != null) put("pitch", pitch)
            if (speed != null) put("speed", speed)
            if (format != null) put("format", format)
            if (sampleRate != null) put("sample_rate", sampleRate)
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
                    listener.onFailure(TTSErrorResponse(WARNING_NO_INTERNET, 0))
                }
            }
        } else {

            handler.post {
                listener.onFailure(
                    TTSErrorResponse(
                        WARNING_MISSING_MANIFEST + requiredPermissions.contentToString(),
                        0
                    )
                )
            }
        }

    }


    private fun request() {
        if (RevSdkConstants.VERBOSE) {
            Log.d(TAG, "request:\n$bodyData")
        }
        if (PermissionUtils.checkManifestPermissions(context) && PermissionUtils.isInternetAvailable(
                context
            )
        ) {
            Http.Request(Http.POST)
                .header(headers)
                .body(bodyData)
                .url(REVERIE_BASE_URL)
                .makeAudioRequest(object : ByteArrayListener {
                    override fun onResponse(res: ByteArray?) {
                        if (res != null) {
                            val wavFileName = "response.wav"
                            val wavFile = File(context.getExternalFilesDir(null), wavFileName)
                            wavFile.writeBytes(res)
                            val ttsAudioData = saveWavFile(res, mContext)
                            handler.post {
                                listener.onSuccess(ttsAudioData)
                            }
                        }
                    }

                    override fun onFailure(e: Exception) {
                        Log.e(TAG, "request:OnFailure\n${e.toString()}")

                        handler.post {
                            listener.onFailure(TTSErrorResponse(e.toString(), 0))
                        }
                    }

                    override fun onError(res: JSONObject) {
                        Log.e(TAG, "request:onError\n${res.toString()}")

                        handler.post {
                            listener.onError(parseErrorJson(res.toString()))
                        }
                    }

                })


        }

    }

    private fun parseErrorJson(jsonResponse: String): TTSErrorResponse {
        try {
            val jsonObject = JSONObject(jsonResponse)

            if (jsonObject.has("message")) {
                val message = jsonObject.getString("message")
                if (jsonObject.has("status")) {
                    val status = jsonObject.getInt("status")
                    return TTSErrorResponse(message, status)
                }
                return TTSErrorResponse(message, 0)
            }
            else if (jsonObject.has("cause")) {
                val message = jsonObject.getString("cause")
                if (jsonObject.has("status")) {
                    val status = jsonObject.getInt("status")
                    return TTSErrorResponse(message, status)
                }
                return TTSErrorResponse(message, 0)
            }
            return TTSErrorResponse(jsonResponse,0)
        }


        catch(e : Exception){
            return TTSErrorResponse(jsonResponse,0)

        }
    }

    private fun saveWavFile(responseBodyByteArray: ByteArray, context: Context): TTSAudioData {
        val wavFileName = "response.wav"
        val wavFile = File(context.getExternalFilesDir(null), wavFileName)
        wavFile.writeBytes(responseBodyByteArray)
        return TTSAudioData(wavFile)
    }

    companion object {
        private const val TAG = "TextToSpeech"
    }

}







