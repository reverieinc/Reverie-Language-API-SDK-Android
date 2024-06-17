package com.reverie.sdk.utilities

import android.media.AudioRecord
import android.util.Log
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RecordingUtility(
    private var bufferSize: Int,
    private var audioFile: File,
    private var isRecording: Boolean,
    private var audioRecord: AudioRecord
) {
    private val sampleRate: Int = 16000
    fun writeAudioDataToFile() {
        Log.d("isRecording", audioFile.toString())
        val data = ByteArray(bufferSize)
        var lastVal: Int
        // Change the file extension as needed (e.g., .w or .a)
        var fos: FileOutputStream?
        try {
            fos = FileOutputStream(audioFile)
        } catch (e: FileNotFoundException) {

            fos = null
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
                        sum += Math.abs(shortBuffer.getShort(0).toInt()).toLong()
                        shortBuffer.clear()
                        i += 2
                    }
                    lastVal = (sum / (bufferSize / 16)).toInt()
                    try {
                        fos.write(data)
                    } catch (e: IOException) {


                    }

                }
            }
            try {
                fos.close()
            } catch (e: IOException) {

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
        } catch (e: FileNotFoundException) {

        } catch (e: IOException) {

        }

    }

    private fun randomAccessFile(file: File): RandomAccessFile {
        val randomAccessFile: RandomAccessFile
        randomAccessFile = try {
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
}