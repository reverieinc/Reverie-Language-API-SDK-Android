package com.reverie.sdk.stt_stream

import android.util.Log
import com.reverie.sdk.utilities.constants.RevSdkConstants

public class LOG {
    companion object{
       public var EXTERNAL_DEBUG=false
      internal  fun customLogger(tag:String, s:String)
        {
            if(RevSdkConstants.VERBOSE)
            {
                Log.d(tag,s);

            }

        }

    }
}

