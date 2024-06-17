package com.reverie.apis

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.reverie.sdk.tts.TTSAudioData
import com.reverie.sdk.tts.TTSErrorResponse
import com.reverie.sdk.tts.TextToSpeech
import com.reverie.sdk.tts.TextToSpeechResultListener
import com.reverie.sdk.utilities.constants.RevSdkConstants
import java.io.IOException

class TtsActivity : AppCompatActivity(), TextToSpeechResultListener {
    private lateinit var speakIV: ImageView
    private lateinit var enteredTextTv: EditText
    private lateinit var textToSpeechObj: TextToSpeech
    private var sourceLanguage: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts)
        RevSdkConstants.VERBOSE = false
        /**
         *
         *  Instance of tts is created and api key , app-id and Context  is passed
         *
         *
         */
        textToSpeechObj = TextToSpeech(
            BuildConfig.REV_API_KEY,
            BuildConfig.REV_APP_ID,
            this,
            this
        )


        initViews()



        speakIV.setOnClickListener {
            val inputString: String = enteredTextTv.text.toString()

            if (inputString.isNotEmpty()) {

                /**
                 *
                 *  Using the instance tts function is created where sentences i.e entered text is passed
                and Context for callback
                 *
                 *
                 */
                textToSpeechObj.speak(
                    inputString,
                    sourceLanguage
                )
            } else {
                Toast.makeText(this, "Please enter some text to speak", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initViews() {
        speakIV = findViewById(R.id.speakIV)
        enteredTextTv = findViewById(R.id.inputET)
        val srcLanguages = resources.getStringArray(R.array.Speakers)
        val spinner = findViewById<Spinner>(R.id.sourceSpinner)
        //source languages spinner
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, srcLanguages
            )
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    sourceLanguage = when (srcLanguages[position]) {
                        "English Female" -> RevSdkConstants.TTSSpeaker.ENGLISH_FEMALE
                        "Hindi Female" -> RevSdkConstants.TTSSpeaker.HINDI_FEMALE
                        "Assamese Female" -> RevSdkConstants.TTSSpeaker.ASSAMESE_FEMALE
                        "Bengali Female" -> RevSdkConstants.TTSSpeaker.BENGALI_FEMALE
                        "Gujarati Female" -> RevSdkConstants.TTSSpeaker.GUJARATI_FEMALE
                        "Kannada Female" -> RevSdkConstants.TTSSpeaker.KANNADA_FEMALE
                        "Malayalam Female" -> RevSdkConstants.TTSSpeaker.MALAYALAM_FEMALE
                        "Marathi Female" -> RevSdkConstants.TTSSpeaker.MARATHI_FEMALE
                        "Odia Female" -> RevSdkConstants.TTSSpeaker.ODIA_FEMALE
                        "Punjabi Female" -> RevSdkConstants.TTSSpeaker.PUNJABI_FEMALE
                        "Tamil Female" -> RevSdkConstants.TTSSpeaker.TAMIL_FEMALE
                        "Telugu Female" -> RevSdkConstants.TTSSpeaker.TELUGU_FEMALE
                        "Hindi Male" -> RevSdkConstants.TTSSpeaker.HINDI_MALE
                        "Assamese Male" -> RevSdkConstants.TTSSpeaker.ASSAMESE_MALE
                        "Bengali Male" -> RevSdkConstants.TTSSpeaker.BENGALI_MALE
                        "Gujarati Male" -> RevSdkConstants.TTSSpeaker.GUJARATI_MALE
                        "Kannada Male" -> RevSdkConstants.TTSSpeaker.KANNADA_MALE
                        "Malayalam Male" -> RevSdkConstants.TTSSpeaker.MALAYALAM_MALE
                        "Marathi Male" -> RevSdkConstants.TTSSpeaker.MARATHI_MALE
                        "Odia Male" -> RevSdkConstants.TTSSpeaker.ODIA_MALE
                        "Punjabi Male" -> RevSdkConstants.TTSSpeaker.PUNJABI_MALE
                        "Tamil Male" -> RevSdkConstants.TTSSpeaker.TAMIL_MALE
                        "Telugu Male" -> RevSdkConstants.TTSSpeaker.TELUGU_MALE
                        // Add other cases for different languages
                        else -> RevSdkConstants.TTSSpeaker.ENGLISH_FEMALE// Default to English or your choice
                    }
                    Log.d("Source Language", sourceLanguage)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do something when nothing is selected (optional)

                }
            }
        }
    }

    override fun onSuccess(file: TTSAudioData) {

        val filepath = file.wavfile.path
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(filepath)
            mediaPlayer.prepare()
            mediaPlayer.start()

            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
        } catch (e: IOException) {
            e.printStackTrace()

        }
    }

    override fun onFailure(error: TTSErrorResponse) {

        enteredTextTv.setText(error.message)

    }

    override fun onError(res: TTSErrorResponse) {

        enteredTextTv.setText(res.message)
    }
}