package com.reverie.sdk.utilities.networking

import android.util.Log
import com.reverie.sdk.utilities.constants.CONNECT_TIMEOUT_TIME
import com.reverie.sdk.utilities.constants.READ_TIMEOUT_TIME
import com.reverie.sdk.utilities.constants.RevSdkConstants
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


object Http {

    const val GET = "GET"
    const val POST = "POST"
    const val DELETE = "DELETE"
    const val PUT = "PUT"
    const val BUFFER_SIZE = 2000

    class Request(internal val method: String) {
        internal val header: MutableMap<String, Any> = HashMap()
        internal var url: String? = null
        internal var body: ByteArray? = null
        private var jsonObjReqListener: JSONObjectListener? = null
        private var bytearrayListener: ByteArrayListener? = null
        private var threadExecutor: ThreadExecutor = ThreadExecutor()

        fun url(url: String?): Request {
            this.url = url
            return this
        }

        fun body(bodyJson: JSONObject): Request {
            val textBody = bodyJson?.toString()
            body = textBody?.toByteArray(Charsets.UTF_8)
            this.header["Content-Type"] = "application/json"
            return this
        }

        fun header(header: Map<String, Any>?): Request {
            if (header.isNullOrEmpty()) return this
            this.header.putAll(header)
            return this
        }

        fun makeRequest(jsonObjectListener: JSONObjectListener): Request {
            this.jsonObjReqListener = jsonObjectListener
            threadExecutor.execute(RequestTask(this))
            return this
        }

        fun makeMultipartRequest(jsonObjectListener: JSONObjectListener, path: String): Request {
            this.jsonObjReqListener = jsonObjectListener
            threadExecutor.execute(RequestTaskMultipart(this, path))
            return this
        }
        fun makeMultipartRequestBatch(jsonObjectListener: JSONObjectListener, path: String): Request {
            this.jsonObjReqListener = jsonObjectListener
            threadExecutor.execute(RequestTaskMultipartBatch(this, path))
            return this
        }

        fun makeAudioRequest(byteArray: ByteArrayListener): Request {
            this.bytearrayListener = byteArray
            threadExecutor.execute(RequestTaskTts(this))
            return this
        }

        internal fun sendAudioResponse(audioBytes: ByteArray, e: Exception?) {
            if (bytearrayListener != null) {
                if (e != null) bytearrayListener?.onFailure(e)
                else bytearrayListener?.onResponse(audioBytes)
            }
        }

        internal fun sendResponse(resp: Response?, e: Exception?) {
            if (jsonObjReqListener != null) {
                if (e != null) jsonObjReqListener?.onException(e)
                else jsonObjReqListener?.onResponse(resp?.asJSONObject())
            }
        }

        internal fun sendErrorResponse(response: Response?) {

            if (jsonObjReqListener != null) {
                jsonObjReqListener?.onError(response?.asJSONObject())
            }


        }

        fun sendErrorResponseTts(response: Response?) {

            if (bytearrayListener != null) {
                bytearrayListener!!.onError(response!!.asJSONObject())
            }
        }

        fun sendErrorInternalServer(message: String, code: Int) {
            val jsonObject = JSONObject()
            jsonObject.put("message", message)
            jsonObject.put("status", code)
            if (bytearrayListener != null) {
                bytearrayListener!!.onError(jsonObject)
            }
        }

        fun sendErrorInternalServerNonTTs(message: String, code: Int) {
            val jsonObject = JSONObject()
            jsonObject.put("message", message)
            jsonObject.put("status", code)
            if (jsonObjReqListener != null) {
                jsonObjReqListener!!.onError(jsonObject)
            }
        }


    }

    internal class RequestTask(private val req: Request) : Runnable {
        override fun run() {
            try {
                val conn = request()
                val status = conn.responseCode
                if (RevSdkConstants.VERBOSE)
                    Log.d("RevAPI", "responseCode= $status")
                val validStatus = status in 200..299
                val parsedResponse = parseResponse(conn)
                if (validStatus) {
                    req.sendResponse(parsedResponse, null)
                } else {
                    if (status in 500..599) {
                        req.sendErrorInternalServerNonTTs(conn.responseMessage, conn.responseCode)
                    } else {
                        try {
                            req.sendErrorResponse(parsedResponse)
                        } catch (e: Exception) {
                            req.sendErrorInternalServerNonTTs(conn.responseMessage, conn.responseCode)
                        }
                    }
                }
            } catch (e: IOException) {
                req.sendErrorInternalServer(e.toString(), 0);
                req.sendResponse(null, e)
            }
        }


        @Throws(IOException::class)
        private fun request(): HttpURLConnection {
            val url = URL(req.url)
            val conn = url.openConnection() as HttpURLConnection
            val method = req.method
            conn.requestMethod = method
            for ((key, value) in req.header) {
                conn.setRequestProperty(key, "$value")
            }
            if (req.body != null) {
                val outputStream = conn.outputStream
                outputStream.write(req.body)
            }
            conn.connect()
            return conn
        }


        @Throws(IOException::class)
        private fun parseResponse(conn: HttpURLConnection): Response {
            try {
                val bos = ByteArrayOutputStream()
                val status = conn.responseCode

                val validStatus = status in 200..299
                val inpStream = if (validStatus) conn.inputStream else conn.errorStream
                var read: Int
                var totalRead = 0
                val buf = ByteArray(BUFFER_SIZE)
                while (inpStream.read(buf).also { read = it } != -1) {
                    val bufferContent = String(buf, 0, read, Charsets.UTF_8)
                    val bytes = bufferContent.toByteArray(Charsets.UTF_8)
                    val numBytes = bytes.size
                    bos.write(buf, 0, read)
                    totalRead += read
                }

                return Response(bos.toByteArray())
            } finally {
                conn.disconnect()
            }
        }

    }

    internal class RequestTaskTts(private val req: Request) : Runnable {
        override fun run() {
            try {
                val conn = request()
                val status = conn.responseCode

                val validStatus = status in 200..299
                val parsedResponse = parseResponseTts(conn)
                if (validStatus) {
                    req.sendAudioResponse(parsedResponse, null)
                    // req.sendResponse(parsedResponse, null)
                } else {
                    if (status in 500..599) {
                        req.sendErrorInternalServer(conn.responseMessage, conn.responseCode)

                    } else {
                        req.sendErrorResponseTts(Response(parsedResponse))
                    }


                }


            } catch (e: IOException) {
                req.sendErrorInternalServer(e.toString(), 0);
//
            }
        }

        private fun request(): HttpURLConnection {
            val url = URL(req.url)
            val conn = url.openConnection() as HttpURLConnection
            val method = req.method
            conn.requestMethod = method
            for ((key, value) in req.header) {
                conn.setRequestProperty(key, "$value")
            }
            if (req.body != null) {
                val outputStream = conn.outputStream
                outputStream.write(req.body)
            }
            conn.connect()
            return conn
        }

        @Throws(IOException::class)
        private fun parseResponseTts(conn: HttpURLConnection): ByteArray {
            try {
                val bos = ByteArrayOutputStream()
                val status = conn.responseCode
                val validStatus = status in 200..299
                val inpStream = if (validStatus) conn.inputStream else conn.errorStream
                var read: Int
                var totalRead = 0
                val buf = ByteArray(BUFFER_SIZE)
                while (inpStream.read(buf).also { read = it } != -1) {
                    bos.write(buf, 0, read)
                    totalRead += read
                }
                return bos.toByteArray()
            } finally {
                conn.disconnect()
            }
        }

    }

    internal class RequestTaskMultipart(private val req: Request, private val path: String) :
        Runnable {
        override fun run()
        {
            try {
                val conn = request()
                val status = conn.responseCode
                val validStatus = status in 200..299
                val parsedResponse = parseResponse(conn)
                if (validStatus) {
                    req.sendResponse(parsedResponse, null)
                } else {
                    if (status in 500..599) {
                        req.sendErrorInternalServerNonTTs(conn.responseMessage, conn.responseCode)
                        Log.d("Http","HTTP:RequestTaskMultipart:rub"+ conn.responseMessage)
                    } else {
                        req.sendErrorResponse((parsedResponse))
                    }


                }

            } catch (e: IOException) {
                req.sendErrorInternalServerNonTTs(e.toString(), 0);
                Log.d("Http","HTTP:RequestTaskMultipart:rub"+ e.toString())
            }
        }

        private fun request(): HttpURLConnection {
            val url = URL(req.url)
            val conn = url.openConnection() as HttpURLConnection
            val method = req.method
            conn.requestMethod = method
            conn.doOutput = true

            conn.connectTimeout = CONNECT_TIMEOUT_TIME
            conn.readTimeout = READ_TIMEOUT_TIME


            for ((key, value) in req.header) {
                conn.setRequestProperty(key, "$value")
            }
            val boundary = "*****"
            val lineEnd = "\r\n"
            val twoHyphens = "--"

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            val outputStream = DataOutputStream(conn.outputStream)


            // Adding file details
            outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"audio_file\";filename=\"audioFile.name\"$lineEnd")
            outputStream.writeBytes("Content-Type: audio/*$lineEnd$lineEnd")
            val fileInputStream = File(path).inputStream()
            fileInputStream.copyTo(outputStream)
            fileInputStream.close()
            outputStream.writeBytes(lineEnd)

            // End of multipart/form-data
            outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")

            outputStream.flush()
            //multipart ends

            conn.connect()
            return conn
        }

        @Throws(IOException::class)
        private fun parseResponse(conn: HttpURLConnection): Response {
            try {
                val bos = ByteArrayOutputStream()
                val status = conn.responseCode
                val validStatus = status in 200..299
                val inpStream = if (validStatus) conn.inputStream else conn.errorStream
                var read: Int
                var totalRead = 0
                val buf = ByteArray(BUFFER_SIZE)
                while (inpStream.read(buf).also { read = it } != -1) {
                    bos.write(buf, 0, read)
                    totalRead += read
                }
                return Response(bos.toByteArray())
            } finally {
                conn.disconnect()
            }
        }
    }
    internal class RequestTaskMultipartBatch(private val req: Request, private val path: String) :
        Runnable {
        override fun run()
        {
            try {
                val conn = batchrequest()
                val status = conn.responseCode
                val validStatus = status in 200..299
                val parsedResponse = parseResponse(conn)
                if (validStatus) {
                    req.sendResponse(parsedResponse, null)
                } else {
                    if (status in 500..599) {
                        req.sendErrorInternalServerNonTTs(conn.responseMessage, conn.responseCode)
                        Log.d("Http","Http:ReuquestTaskMultipartBatch"+ conn.responseMessage)
                    } else {
                        req.sendErrorResponse((parsedResponse))
                    }


                }

            } catch (e: IOException) {
                req.sendErrorInternalServerNonTTs(e.toString(), 0);
                Log.d("Http","Http:ReuquestTaskMultipartBatch"+ e.toString())
            }
        }


        private fun batchrequest(): HttpURLConnection {
            val url = URL(req.url)
            val conn = url.openConnection() as HttpURLConnection
            val method = req.method
            conn.requestMethod = method
            conn.doOutput = true

            conn.connectTimeout = CONNECT_TIMEOUT_TIME
            conn.readTimeout = READ_TIMEOUT_TIME


            for ((key, value) in req.header) {
                conn.setRequestProperty(key, "$value")
            }
            val boundary = "*****"
            val lineEnd = "\r\n"
            val twoHyphens = "--"

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            val outputStream = DataOutputStream(conn.outputStream)


            // Adding file details
            outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"$path\"$lineEnd")
            outputStream.writeBytes("Content-Type: audio/*$lineEnd$lineEnd")
            val fileInputStream = File(path).inputStream()
            fileInputStream.copyTo(outputStream)
            fileInputStream.close()
            outputStream.writeBytes(lineEnd)

            // End of multipart/form-data
            outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")

            outputStream.flush()
            //multipart ends

            conn.connect()
            return conn
        }

        @Throws(IOException::class)
        private fun parseResponse(conn: HttpURLConnection): Response {
            try {
                val bos = ByteArrayOutputStream()
                val status = conn.responseCode
                val validStatus = status in 200..299
                val inpStream = if (validStatus) conn.inputStream else conn.errorStream
                var read: Int
                var totalRead = 0
                val buf = ByteArray(BUFFER_SIZE)
                while (inpStream.read(buf).also { read = it } != -1) {
                    bos.write(buf, 0, read)
                    totalRead += read
                }
                return Response(bos.toByteArray())
            } finally {
                conn.disconnect()
            }
        }
    }

    class Response(private val data: ByteArray) {
        @Throws(JSONException::class)
        fun asJSONObject(): JSONObject {
            val str = String(data, StandardCharsets.UTF_8)
            if (RevSdkConstants.VERBOSE) {
                Log.d("RevAPI", "response: $str")
            }
            return if (str.isEmpty()) JSONObject() else JSONObject(str)
        }
    }
}