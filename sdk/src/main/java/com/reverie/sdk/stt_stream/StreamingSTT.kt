/*
 * All Rights Reserved. Copyright 2023. Reverie Language Technologies Limited.(https://reverieinc.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reverie.sdk.stt_stream

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.reverie.sdk.stt_stream.LOG.Companion.customLogger
import com.reverie.sdk.utilities.PermissionUtils.Companion.checkManifestPermissions
import com.reverie.sdk.utilities.PermissionUtils.Companion.checkPermissionsAudio
import com.reverie.sdk.utilities.PermissionUtils.Companion.isInternetAvailable
import com.reverie.sdk.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.sdk.utilities.constants.WARNING_NO_INTERNET
import com.reverie.sdk.utilities.constants.WARNING_PERMISSIONS_GRANT_REQUIRED
import com.reverie.voiceinput.business.CustomAudioRecorder
import com.reverie.voiceinput.business.CustomSocketListener
import java.io.File
import java.util.Objects


/** The Speech-to-Text accurately converts speech into text using an API powered by Reverie's AI technology. The solution will transcribe the speech in real-time of various Indian languages and audio formats.
 *  @param apiKey:  pass the apikey
 *  @param appId:  pass the app_id
````
>**/
class StreamingSTT(
    context: Context?,
    private var apiKey: String,
    private var appId: String
) :
    CustomAudioRecorder.RecordingStateListener {

    private var mContext: Context? = context
    private var customAudioRecorder: CustomAudioRecorder
    lateinit var streamingSTTResultListener: StreamingSTTResultListener
    private lateinit var langCode: String
    private lateinit var domain: String
    private var noInputTimeout = 2.0
    private var silence = 1.0
    private var timeout = 15.0
    val handler = Handler(Looper.getMainLooper())
    private var isFinal = false

    private val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO
    )

    fun setOnResultListener(listener: StreamingSTTResultListener) {
        this.streamingSTTResultListener = listener
        customLogger(TAG, "Listener")

    }

    private var customSocketListener: CustomSocketListener? = null

    init {
        customSocketListener = CustomSocketListener()
        // TODO: check if folder creation is not necessary
        mFileName = Objects.requireNonNull(
            mContext!!.getExternalFilesDir(null)
        )?.absolutePath + "/RevSttRecord"
        customLogger(TAG, "File name" + mFileName)
        val direct = File(mFileName)
        if (!direct.exists()) {
            direct.mkdir()
        }
        mFileName += "/recorded_audio.wav"
        customAudioRecorder = CustomAudioRecorder.getInstance(this)
        customLogger(TAG, "init: customAudioRecorder=${customAudioRecorder}")
      //  customAudioRecorder.setOutputFile(mFileName)
        customAudioRecorder.setSpeechClient(customSocketListener)
        customSocketListener!!.setEventCallback(object : CustomSocketListener.EventCallback {
            override fun onEvent(stage: Int) {
                customLogger(TAG, "onEvent: stage= $stage")

            }
        })
        customSocketListener!!.setOnResultListener(object : InternalResultListener {
            override fun onResult(result: StreamingSTTResultData?) {

                handler.post {
                    streamingSTTResultListener!!.onResult(result)
                }

                if (result?.final == true) {
                    inProcess = false

                    if (!isEOF) {
                        stopRecognitions()
                    }
                    isFinal = true

                }
            }

            override fun onError(result: StreamingSTTErrorResponseData) {
                isEOF = false
                if (!isFinal) {
                    handler.post {

                        if (inProcess) {

                            cancelRecognitions()
                        }
                        inProcess = false
                        streamingSTTResultListener.onError(result)
                    }
                }
            }
        })

    }


    private fun addData(data: ByteArray) {
        customSocketListener?.addData(data)
    }

    fun setNoInputTimeout(noInputTimeout: Double) {

        this.noInputTimeout = noInputTimeout
    }

    fun setSilence(silence: Double) {

        this.silence = silence
    }

    fun setTimeout(timeout: Double) {
        this.timeout = timeout

    }

    /**
     * To track details of STT-API in backend on a deeper level, used for debugging in case of error
     * @param status the boolean to turn it ON and OFF
     */
    fun setApiDebug(status: Boolean) {
        LOG.EXTERNAL_DEBUG = status
    }

    /**
     *  @param langCode Indicates the language in which the audio is spoken.
     *  @param domain The universe in which the Streaming STT API is used for transcribing the speech.
     *  @see <a href="https://docs.reverieinc.com/reference/speech-to-text-streaming-api#supporting-domain"> Available domains</a>
     *  @see <a href="https://docs.reverieinc.com/reference/speech-to-text-streaming-api/language-codes">Language Codes</a>
     */
    fun startRecognitions(langCode: String, domain: String, logging: String) {
        isEOF = false
        if (inProcess) {
            return
        }
        isFinal = false
        if (mContext?.let { checkManifestPermissions(it, *requiredPermissions) } == true) {
            if (mContext?.let { checkPermissionsAudio(it) } == true) {
                if (isInternetAvailable(mContext!!)) {
                    this.langCode = langCode
                    this.domain = domain
                    try {

                        customAudioRecorder = CustomAudioRecorder.getInstance(this)
                        customLogger(
                            TAG,
                            "startRecognitions: customAudioRecorder=${customAudioRecorder}"
                        )

//                        customAudioRecorder.setOutputFile(mFileName)
                        customAudioRecorder.setSpeechClient(customSocketListener)
                        inProcess = true
                        customLogger("onErrorStop", inProcess.toString())
                        customAudioRecorder.prepare()
                        customSocketListener!!.setSilence(silence)
                        customSocketListener!!.setTimeout(timeout)
                        customSocketListener!!.setNoInputTimeout(noInputTimeout)

                        //customSocketListener!!.startRequest(langCode, domain, apikey, app_id)
                        customSocketListener!!.startRequest(
                            langCode,
                            domain,
                            apiKey,
                            appId,
                            logging
                        )
                        customAudioRecorder.startRecordingProcess()

                        customLogger("TAG", "*********** Recording start ***********")
                        isRecording = true


                    } catch (e: Exception) {
                        customLogger(TAG, "prepare() failed due to: $e")
                        e.message?.let { parseError(it, 0) }
                            ?.let { streamingSTTResultListener.onError(it) }

                    }
                } else {
                    handler.post {
                        inProcess = false
                        streamingSTTResultListener.onError(
                            parseError(
                                WARNING_NO_INTERNET,
                                0
                            )
                        )
                    }


                }
            } else {
                handler.post {
                    inProcess = false
                    streamingSTTResultListener.onError(
                        parseError(
                            WARNING_PERMISSIONS_GRANT_REQUIRED,
                            0
                        )
                    )
                }
            }
        } else {
            handler.post {
                inProcess = false
                streamingSTTResultListener.onError(
                    parseError(
                        WARNING_MISSING_MANIFEST,
                        0
                    )

                )
            }
        }
    }

    fun stopRecognitions() {

//        if (!isRecording) {
//    x        return
//        }

        if (customAudioRecorder != null && isRecording) {
            customSocketListener!!.endRequest()

            customAudioRecorder.stop()
            customAudioRecorder.release()
            customAudioRecorder.reset()
        }
        isRecording = false
        inProcess = false
    }

    fun cancelRecognitions() {

        if (customAudioRecorder != null && isRecording) {

            customSocketListener!!.cancelRequest()

            customAudioRecorder.stop()
            customAudioRecorder.release()
            customAudioRecorder.reset()
            isEOF = false
        }
        isRecording = false
        inProcess = false
        //   StreamingSTT.inProcess = false
    }

    companion object {
        private const val TAG = "RecordingUtility"
        private var isRecording = false
        private var mFileName: String? = null
        internal var inProcess: Boolean = false
        internal var isEOF: Boolean = false
    }


    private fun parseError(message: String, code: Int): StreamingSTTErrorResponseData {

        return StreamingSTTErrorResponseData(message, code)
    }

    override fun recordingStart(start: Boolean) {
        streamingSTTResultListener.onRecordingStart(start)
    }

    override fun recordingEnd(stop: Boolean) {
        inProcess = false
        handler.post { streamingSTTResultListener.onRecordingEnd(stop) }


    }

    override fun recordingData(data: ByteArray, amplitude: Int) {
        streamingSTTResultListener.onRecordingData(data, amplitude)
    }

}