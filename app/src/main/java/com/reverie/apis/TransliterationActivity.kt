package com.reverie.apis

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.reverie.sdk.transliteration.Transliteration
import com.reverie.sdk.transliteration.TransliterationData
import com.reverie.sdk.transliteration.TransliterationError
import com.reverie.sdk.transliteration.TransliterationResultListener
import com.reverie.sdk.utilities.constants.RevSdkConstants

class TransliterationActivity : AppCompatActivity(), TransliterationResultListener {
    private lateinit var inputET: EditText
    private lateinit var convertBtn: FloatingActionButton
    private var domain: Int = RevSdkConstants.TransliterationDomain.DEFAULT
    var sourceLanguage: String = ""
    var targetLanguage: String = ""
    private lateinit var resultTv: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transliteration)
        RevSdkConstants.VERBOSE = false


        initViews()

        /**Instance Of Transliteration class is created and apikey and app id, and domain is passed as parameters*/

        val transliterationObj = Transliteration(
            BuildConfig.REV_API_KEY,
            BuildConfig.REV_APP_ID, domain,
            this,
            this
        )
        transliterationObj.noOfSuggestions = 2
        transliterationObj.isBulk = true
        transliterationObj.abbreviate = true
        transliterationObj.ignoreTaggedEntities = true
        transliterationObj.convertOrdinal = true
        transliterationObj.abbreviationWithoutDot = true

        //here we have to take the text from edit text

        convertBtn.setOnClickListener {
            val sentences: List<String> = listOf(inputET.text.toString())


            /**Using the instance transliterate function is called where data i.e entered sentence and source language and
            target language and Context for CallBack is created*/
            transliterationObj.transliterate(
                data = sentences,
                //here the dynamic values will come
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage
            )


        }
    }

    private fun initViews() {

        val srcLanguages = resources.getStringArray(R.array.Languages)
        val targetLanguages = resources.getStringArray(R.array.Languages)
        inputET = findViewById(R.id.inputET)
        convertBtn = findViewById(R.id.convertBtn)
        resultTv = findViewById(R.id.resultTv)
        val spinner = findViewById<Spinner>(R.id.sourceSpinner)
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
                    }
                }
            }


        }
    }

    override fun onSuccess(response: TransliterationData) {
        resultTv.setText(response.responseList[0].outString)

    }

    override fun onFailure(error: TransliterationError) {
        resultTv.setText(error.toString())
    }


}