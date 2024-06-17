package com.reverie.apis

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.reverie.sdk.stt_batch.BatchSTT
import com.reverie.sdk.stt_batch.BatchSTTErrorResponseData
import com.reverie.sdk.stt_batch.BatchSTTResultListener
import com.reverie.sdk.stt_batch.BatchStatusResponse
import com.reverie.sdk.stt_batch.BatchTranscriptResponse
import com.reverie.sdk.stt_batch.BatchUploadResponse
import com.reverie.sdk.utilities.PathUtil
import com.reverie.sdk.utilities.constants.RevSdkConstants
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.properties.Delegates

class BatchSttActivity : AppCompatActivity(), BatchSTTResultListener {
    private lateinit var jobId: String
    var sourceLanguage: String = ""
    private var mFileName: String? = null
    private var audioRecord: AudioRecord? = null
    private var bufferSize: Int = 0
    private var isRecording = false
    private var framePeriod: Int = 0
    private var directory: File? = null
    private lateinit var audioFile: File
    private lateinit var recordingThread: Thread
    private var lastVal by Delegates.notNull<Int>()
    private lateinit var startRecording: LinearLayout
    private lateinit var transcribeTV: TextView
    private lateinit var outputText: TextView
    private lateinit var uploadFile: LinearLayout
    private lateinit var filename: TextView
    private lateinit var batchSTTInstance: BatchSTT;
    private lateinit var progressBar: ProgressBar
    private lateinit var checkStatus: Button
    private lateinit var seeTranscript: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batch_stt)
        requestPermissions()
        initViews()
        RevSdkConstants.VERBOSE = true
        //Required values for audio recording
        bufferSize = framePeriod * 2 * 16 * 1 / 8
        framePeriod = RECORDER_SAMPLERATE * 10 / 1000
        directory = getExternalFilesDir(null)
        audioFile = File(directory, "MyRecording.wav")
        mFileName = audioFile.absolutePath
        seeTranscript.isVisible = false

        /**
         *
         * Initializing the BatchSTT instance
         *
         *
         */

        batchSTTInstance = BatchSTT(
            BuildConfig.REV_API_KEY,
            BuildConfig.REV_APP_ID,
            this,
            applicationContext
        )
        batchSTTInstance.format = "16k_int16"
        startRecording.setOnClickListener {
            if (isRecording) {
                stopRecordingAudio()
            } else {
                transcribeTV.text = "Listening.."
                startRecordingAudio()

            }
        }
        uploadFile.setOnClickListener {
            pickAudioFile()
        }
    }

    private fun initViews() {
        startRecording = findViewById(R.id.startRecordingLL)
        startRecording.setBackgroundColor(Color.RED)
        filename = findViewById(R.id.filename)
        transcribeTV = findViewById(R.id.transcribeTV)
        uploadFile = findViewById(R.id.upload_file)
        val srcLanguages = resources.getStringArray(R.array.Languages)
        val srcLangSpinner = findViewById<Spinner>(R.id.sourceSpinner)
        progressBar = findViewById(R.id.progressBarUpload)
        checkStatus = findViewById(R.id.checkStatus)
        outputText = findViewById(R.id.output_text)
        seeTranscript = findViewById(R.id.see_transcript_btn)
        //source languages spinner
        if (srcLangSpinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, srcLanguages
            )
            srcLangSpinner.adapter = adapter
            srcLangSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                }
            }
        }
//This button will show the status of the file uploaded
        checkStatus.setOnClickListener {
            //call the BatchStatus using the jobid
            if (this::jobId.isInitialized) {
                outputText.setText("")
//  

                batchSTTInstance.checkStatus(jobId)
                progressBar.visibility = View.VISIBLE
            }
        }
        //Clicking on this will show the transcript received by uploading the file
        seeTranscript.setOnClickListener {
            if (this::jobId.isInitialized) {
                batchSTTInstance.getTranscript(jobId)
            }
        }
    }

    private fun pickAudioFile() {
        // requestPermissions()
        val pickIntent = Intent()
        pickIntent.type = "audio/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT
        pickMediaResultLauncher.launch(pickIntent)
    }

    private val pickMediaResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data!!.data
                if (uri != null) {
                    val realPathFromURI = PathUtil.getRealPathFromURI(this, uri)
                    if (realPathFromURI != null) {
                        val yourArray: List<String> = realPathFromURI.split("/")
                        filename.text = yourArray.lastOrNull()
                        /**
                         *
                         * Sending the audio file
                         *
                         *
                         */
                        sendAudio(realPathFromURI.toString())
                    } else
                        Log.e("URI PROBLEM", "realPathFromURI null")
                }
            }
        }

    /**
     * Use this method to send the audio file for conversion
     * @param path Absolute path of the audio file, which needs to be converted to text
     */
    private fun sendAudio(path: String) {
        Log.d(TAG, "Sending audio file from: $path")
        //Provide the file path for converting to text
        batchSTTInstance.uploadAudio(path, RevSdkConstants.SttDomain.GENERIC, sourceLanguage)


    }

    private fun stopRecordingAudio() {
        if (isRecording) {
            isRecording = false

            // Stop and release the resources
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null

            Log.d("Recording", "Recording Stopped")

//            Sending the audio
            startRecording.setBackgroundColor(Color.RED)
            transcribeTV.text = "Transcribe"


            /**
             *
             * Sending the audio file
             *
             *
             */

            sendAudio(audioFile.absolutePath)
        }
    }

    private fun startRecordingAudio() {
        //Considering Record_Audio Permissions granted in the Mobile and Manifest side
        //requestPermissions()
        if (checkRecordingPermission()) {

            startRecording.setBackgroundColor(Color.GRAY)
            bufferSize = AudioRecord.getMinBufferSize(
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING
            )

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize
            )

            audioRecord?.startRecording()
            isRecording = true

            recordingThread = Thread({ this.writeAudioDataToFile() }, "AudioRecorder Thread")
            recordingThread.start()


        }
    }

    private fun writeAudioDataToFile() {
        val data = ByteArray(bufferSize)
        val fos: FileOutputStream? = try {
            FileOutputStream(audioFile)
        } catch (e: FileNotFoundException) {
            null
        }
        if (null != fos) {
            var chunksCount = 0
            val shortBuffer = ByteBuffer.allocate(2)
            shortBuffer.order(ByteOrder.LITTLE_ENDIAN)
            //TODO: Disable loop while pause.
            while (isRecording) {

                chunksCount += audioRecord!!.read(data, 0, bufferSize)
                if (AudioRecord.ERROR_INVALID_OPERATION != chunksCount) {
                    var sum: Long = 0
                    var i = 0
                    while (i < bufferSize) {

                        //TODO: find a better way to covert bytes into shorts.
                        shortBuffer.put(data[i])
                        shortBuffer.put(data[i + 1])
                        sum += abs(shortBuffer.getShort(0).toInt()).toLong()
                        shortBuffer.clear()
                        i += 2
                    }
                    lastVal = (sum / (bufferSize / 16)).toInt()
                    try {
                        fos.write(data)
                    } catch (_: IOException) {


                    }

                }
            }
            try {
                fos.close()
            } catch (_: IOException) {

            }
            setWaveFileHeader(audioFile, 1)
        }
    }

    private fun setWaveFileHeader(audioFile: File, i: Int) {
        val fileSize: Long = audioFile.length() - 8
        val totalSize = fileSize + 36
        val byteRate: Long =
            (sampleRate * i * (16 / 8)).toLong() //2 byte per 1 sample for 1 channel.


        try {
            val wavFile: RandomAccessFile = randomAccessFile(audioFile)
            wavFile.seek(0) // to the beginning
            wavFile.write(
                generateHeader(
                    fileSize,
                    totalSize,
                    sampleRate.toLong(),
                    1,
                    byteRate
                )
            )
            wavFile.close()
        } catch (_: FileNotFoundException) {

        } catch (_: IOException) {

        }

    }

    private fun randomAccessFile(file: File): RandomAccessFile {
        val randomAccessFile: RandomAccessFile = try {
            RandomAccessFile(file, "rw")
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }
        return randomAccessFile
    }

    private fun generateHeader(
        totalAudioLen: Long, totalDataLen: Long, longSampleRate: Long, channels: Int,
        byteRate: Long
    ): ByteArray? {
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xffL).toByte()
        header[5] = (totalDataLen shr 8 and 0xffL).toByte()
        header[6] = (totalDataLen shr 16 and 0xffL).toByte()
        header[7] = (totalDataLen shr 24 and 0xffL).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 //16 for PCM. 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xffL).toByte()
        header[25] = (longSampleRate shr 8 and 0xffL).toByte()
        header[26] = (longSampleRate shr 16 and 0xffL).toByte()
        header[27] = (longSampleRate shr 24 and 0xffL).toByte()
        header[28] = (byteRate and 0xffL).toByte()
        header[29] = (byteRate shr 8 and 0xffL).toByte()
        header[30] = (byteRate shr 16 and 0xffL).toByte()
        header[31] = (byteRate shr 24 and 0xffL).toByte()
        header[32] =
            (channels * (16 / 8)).toByte() // block align
        header[33] = 0
        header[34] =
            16.toByte() // bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xffL).toByte()
        header[41] = (totalAudioLen shr 8 and 0xffL).toByte()
        header[42] = (totalAudioLen shr 16 and 0xffL).toByte()
        header[43] = (totalAudioLen shr 24 and 0xffL).toByte()
        return header
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this method is called when the user grants the permission for audio recording.
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        // Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG)
                        //   .show()
                    }
                }
            }
        }
    }


    private fun checkRecordingPermission(): Boolean {
        return checkRecordingPermission(applicationContext)
    }


    private fun checkRecordingPermission(context: Context): Boolean {
        val recording = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        return recording == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        // This method is used to request permissions for audio recording and storage.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, // Make sure to cast to an Activity if needed
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_AUDIO
                ),
                REQUEST_AUDIO_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this, // Make sure to cast to an Activity if needed
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                REQUEST_AUDIO_PERMISSION_CODE
            )
        }

    }


    override fun onTranscriptSuccess(response: BatchTranscriptResponse) {
        Log.d(TAG, "onTranscriptSuccess: response= $response")
        progressBar.visibility = View.GONE
        outputText.text = response.result.transcript

    }

    override fun onFailure(error: BatchSTTErrorResponseData) {
        Log.d(TAG + "error", error.message)
        progressBar.visibility = View.GONE
        outputText.text = error.message
    }

    override fun onStatusSuccess(status: BatchStatusResponse) {
        //  outputText.text=status.status
        progressBar.visibility = View.GONE
        seeTranscript.isVisible = true

        outputText.text = status.message


    }

    override fun onUploadSuccess(response: BatchUploadResponse) {

        jobId = response.jobid
        outputText.text = response.jobid
    }

    companion object {
        private const val RECORDER_SAMPLERATE = 16000 // 16kHz
        private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
        private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
        private const val sampleRate: Int = 16000

        // constant for storing audio permission
        const val REQUEST_AUDIO_PERMISSION_CODE = 1
        private const val TAG = "BatchSttActivity"
    }
}