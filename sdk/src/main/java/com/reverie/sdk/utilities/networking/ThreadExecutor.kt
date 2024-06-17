package com.reverie.sdk.utilities.networking

import android.os.Handler
import android.os.HandlerThread
import android.os.Process

class ThreadExecutor {
    fun execute(runnable: Runnable?) {
        val handlerThread = HandlerThread(
            "PriorityHandlerThread",
            Process.THREAD_PRIORITY_DEFAULT
        )
        handlerThread.start();
        val looper = handlerThread.looper
        val handler = Handler(looper)
        handler.post(runnable!!)
    }
}