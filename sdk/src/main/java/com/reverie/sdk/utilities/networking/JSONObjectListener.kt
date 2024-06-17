package com.reverie.sdk.utilities.networking

import org.json.JSONObject

interface JSONObjectListener {
    fun onResponse(res: JSONObject?)
    fun onException(e: Exception?)

    fun onError(res: JSONObject?)

}