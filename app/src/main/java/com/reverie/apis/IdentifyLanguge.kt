package com.reverie.apis

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.reverie.sdk.language_identification.TextLanguageDetectionError
import com.reverie.sdk.language_identification.TextLanguageDetectionListener
import com.reverie.sdk.language_identification.TextLanguageDetectionResult
import com.reverie.sdk.language_identification.TextLanguageDetector
import com.reverie.sdk.utilities.constants.RevSdkConstants

class IdentifyLanguge : AppCompatActivity(), TextLanguageDetectionListener {
    private lateinit var result: EditText
    private lateinit var input: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_languge)
        result = findViewById(R.id.resultTv)
        input = findViewById(R.id.inputET)
        val show_lang = findViewById<FloatingActionButton>(R.id.convertBtn)
        RevSdkConstants.VERBOSE = false
        show_lang.setOnClickListener {
            val identifyInstance = TextLanguageDetector(
                BuildConfig.REV_API_KEY,
                BuildConfig.REV_APP_ID,
                this,
                this
            )
            identifyInstance.maxlength = 2
            identifyInstance.identifyLanguage(input.text.toString())
        }

    }

    override fun onSuccess(response: TextLanguageDetectionResult) {
        result!!.setText(response.toString())
    }

    override fun onFailure(error: TextLanguageDetectionError) {
        result!!.setText(error.toString())
    }
}