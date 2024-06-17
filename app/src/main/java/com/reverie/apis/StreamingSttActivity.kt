package com.reverie.apis

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.reverie.sdk.stt_stream.LOG
import com.reverie.sdk.stt_stream.StreamingSTT
import com.reverie.sdk.stt_stream.StreamingSTTErrorResponseData
import com.reverie.sdk.stt_stream.StreamingSTTResultData
import com.reverie.sdk.stt_stream.StreamingSTTResultListener
import com.reverie.sdk.utilities.constants.RevSdkConstants

class StreamingSttActivity : AppCompatActivity() {

    private lateinit var streamingSTT: StreamingSTT
    private lateinit var startRecordLL: LinearLayout
    lateinit var outputTv: TextView
    private lateinit var transcribeTV: TextView
    private lateinit var spin: Spinner
    var targetLanguage: String = ""
    private val TAG = "StreamingSttActivity"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stt_stream)
        RevSdkConstants.VERBOSE = false
        if (!checkRecordingPermission(this))
            requestRecordPermission()

        initViews()

        /**
         *
         * Initializing the Streaming STT instance
         *
         *
         */

        streamingSTT = StreamingSTT(
            this,
            BuildConfig.REV_API_KEY,
            BuildConfig.REV_APP_ID
        )


        streamingSTT.setSilence(2.0)
        streamingSTT.setTimeout(3.0)
        streamingSTT.setApiDebug(true)
        streamingSTT.setOnResultListener(object : StreamingSTTResultListener {

            override fun onResult(result: StreamingSTTResultData?) {
                //Log.e("response Activity", result.toString())
                if (result != null) {
                    outputTv.text = result.display_text

                    if (result.final) {
                        transcribeTV.text = "Speak"
                    }
                }
            }


            override fun onError(result: StreamingSTTErrorResponseData) {
                if (!result.error.contentEquals("Connection reset")) {
                    outputTv.text = result.error
                }
                transcribeTV.text = "Speak"


            }


            override fun onRecordingStart(isTrue: Boolean) {
                Log.d(TAG, "onRecordingStart: $isTrue")
                transcribeTV.text = "Listening"
            }

            override fun onRecordingEnd(isTrue: Boolean) {
                Log.d(TAG, "onRecordingEnd: $isTrue")
                transcribeTV.text = "Speak"
            }


            override fun onRecordingData(data: ByteArray, amplitude: Int) {
                Log.d(TAG, "onRecordingData: $data")
            }


        })

        startRecordLL.setOnClickListener {
            // Log.d("Streaming Activity", targetLanguage)

            /**
             *
             *
             * Starting the recording process
             *
             *
             *
             */
            streamingSTT.startRecognitions(
                targetLanguage,
                RevSdkConstants.SttDomain.GENERIC,
                RevSdkConstants.SttStreamingLog.TRUE
            );
            LOG.EXTERNAL_DEBUG = true


        }
    }

    private fun initViews() {
        startRecordLL = findViewById(R.id.startRecordingLL)
        transcribeTV = findViewById(R.id.transcribeTV)
        spin = findViewById(R.id.sourceSpinner)
        outputTv = findViewById(R.id.output_text)


        val targetLanguages = resources.getStringArray(R.array.Languages)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, targetLanguages)
        spin.adapter = adapter

        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                targetLanguage = when (targetLanguages[position]) {
                    "ENGLISH" -> RevSdkConstants.Language.ENGLISH
                    "HINDI" -> RevSdkConstants.Language.HINDI
                    "ASSAMESE" -> RevSdkConstants.Language.ASSAMESE
                    "BENGALI" -> RevSdkConstants.Language.BENGALI
                    "GUJARATI" -> RevSdkConstants.Language.GUJARATI
                    "KANNADA" -> RevSdkConstants.Language.KANNADA
                    "MALAYALAM" -> RevSdkConstants.Language.MALAYALAM
                    "MARATHI" -> RevSdkConstants.Language.MARATHI
                    "ODIA" -> RevSdkConstants.Language.ODIA
                    "PUNJABI" -> RevSdkConstants.Language.PUNJABI
                    "TAMIL" -> RevSdkConstants.Language.TAMIL
                    "TELUGU" -> RevSdkConstants.Language.TELUGU
                    // Add other cases for different languages
                    else -> RevSdkConstants.Language.ENGLISH
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do something when nothing is selected (optional)
            }
        }

    }

    fun checkRecordingPermission(context: Context): Boolean {
        val recording = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        return recording == PackageManager.PERMISSION_GRANTED
    }

    fun requestRecordPermission() {
        ActivityCompat.requestPermissions(
            this, // Make sure to cast to an Activity if needed
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
            ),
            99
        )
    }
}