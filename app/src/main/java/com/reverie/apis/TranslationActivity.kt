package com.reverie.apis

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.reverie.sdk.translation.Translation
import com.reverie.sdk.translation.TranslationData
import com.reverie.sdk.translation.TranslationError
import com.reverie.sdk.translation.TranslationResultListener
import com.reverie.sdk.utilities.constants.RevSdkConstants

class TranslationActivity : AppCompatActivity(), TranslationResultListener {
    private lateinit var inputET: EditText
    private lateinit var translateBtn: FloatingActionButton
    var sourceLanguage: String = ""
    var targetLanguage: String = ""
    private lateinit var resultTv: TextView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation)
        RevSdkConstants.VERBOSE = false
        /**Translation instance is created and api key and appid is passed as parameters*/
        val translationInstance = Translation(
            BuildConfig.REV_API_KEY,
            BuildConfig.REV_APP_ID,
            this,
            RevSdkConstants.TranslationDomain.GENERAL,
            this
        )
        translationInstance.dbLookUpParam = true
        translationInstance.segmentationParam = true
        translationInstance.nmtParam = true
        translationInstance.builtInPreProc = true
        translationInstance.debugMode = true


        translationInstance.nmtMask = true
        translationInstance.setNmtMaskTerms("Hello", "This is for testing purpose")
        initViews()


        translateBtn.setOnClickListener {

            /***/

            translationInstance.translate(
                listOf(inputET.text.toString()),
                sourceLanguage,
                targetLanguage,


                )
        }
    }

    private fun initViews() {
        val srcLanguages = resources.getStringArray(R.array.Languages)
        val targetLanguages = resources.getStringArray(R.array.Languages)
        translateBtn = findViewById(R.id.convertBtn)
        inputET = findViewById(R.id.inputET)
        resultTv = findViewById(R.id.resultTv)
        val spinner = findViewById<Spinner>(R.id.sourceSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, srcLanguages
            )
            spinner.adapter = adapter
            spinner.setSelection(1)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    sourceLanguage = when (srcLanguages[position]) {
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
                        else -> RevSdkConstants.Language.ENGLISH// Default to English or your choice
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do something when nothing is selected (optional)

                }
            }
            val spin = findViewById<Spinner>(R.id.targetSpinner)
            if (spin != null) {
                val arrayAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, targetLanguages)
                spin.adapter = arrayAdapter

                spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        targetLanguage = when (srcLanguages[position]) {
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


        }
    }

    override fun onSuccess(response: TranslationData) {

        resultTv.text = response.responseList[0].outString
    }

    override fun onFailure(error: TranslationError) {
        resultTv.text = error.message
    }
}