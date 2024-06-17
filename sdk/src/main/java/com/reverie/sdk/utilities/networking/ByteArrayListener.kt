package com.reverie.sdk.utilities.networking

import org.json.JSONObject

interface ByteArrayListener {

    fun onResponse(res: ByteArray?)
    fun onFailure(e: Exception)

    fun onError(res: JSONObject)

}