package com.reverie.apis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val goToTransliteration = findViewById<LinearLayout>(R.id.goToTransliteration)
        val gotToTts = findViewById<LinearLayout>(R.id.gotToTts)
        val goToTranslation = findViewById<LinearLayout>(R.id.goToTranslation)
        val goToStt = findViewById<LinearLayout>(R.id.goToStt)
        val goToSttBatch = findViewById<LinearLayout>(R.id.goToSttBatch)
        val goToSttStream: LinearLayout = findViewById(R.id.goToSttStream)
        val check_code: Button = findViewById<Button>(R.id.check_code)
        val lang_identify: LinearLayout = findViewById(R.id.goToIdentification)
        goToTransliteration.setOnClickListener {
            val intent = Intent(this, TransliterationActivity::class.java)
            startActivity(intent)

        }
        gotToTts.setOnClickListener {
            val intent = Intent(this, TtsActivity::class.java)
            startActivity(intent)
        }

        goToTranslation.setOnClickListener {
            val intent = Intent(this, TranslationActivity::class.java)
            startActivity(intent)
        }
        goToStt.setOnClickListener {
            val intent = Intent(this, FileSttActivity::class.java)
            startActivity(intent)
        }
        goToSttBatch.setOnClickListener {
            val intent = Intent(this, BatchSttActivity::class.java)
            startActivity(intent)
        }
        goToSttStream.setOnClickListener {
            val intent = Intent(this, StreamingSttActivity::class.java)
            startActivity(intent)
        }
        lang_identify.setOnClickListener {
            val intent = Intent(this, IdentifyLanguge::class.java)
            startActivity(intent)
        }
    }
}



