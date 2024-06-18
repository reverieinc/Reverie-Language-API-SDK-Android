# Speech to Text | Streaming
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

The Speech-to-Text accurately converts speech into text using an API powered by Reverie's AI technology. The solution will transcribe the speech in real-time of various Indian languages and audio formats.
> **Note: On the basis of the selected [domain](#supporting-domain) the result will vary**
## Key Features
The Speech-to-Text solution offers robust features that help in delivering better user experience in interacting with the products through voice commands:

- Real-time Transcription:
The pre-recorded audio files are transcribed accurately into text format in real-time. It will decode speech with high accuracy and confidence, even from the lower-quality audio input.

- Personalize Speech Model:
Tailor speech recognition to transcribe domain-specific terms and boost your transcription accuracy of specific words or phrases.

- Noise Resistance:
The solution will decode moderate noisy audio data recorded in various environments without requiring additional noise cancellation.

- Content Filtering:
Obscenity filter will detect inappropriate or unprofessional content in your audio data and filter out profane words in text output.

###  Supporting Languages

The solution will understand regional accents, bi-lingual nature of Indians, and is dialect-agnostic. it will transcribe the audio from widely spoken Indian languages:
.


| Language  | Code | Language | Code |
|-----------|------|----------|------|
| Hindi     | hi   | Bengali  | bn   |
| Gujarati  | gu   | Kannada  | kn   |
| Tamil     | ta   | Punjabi  | pa   |
| Telugu    | te   | English  | en   |
| Marathi   | mr   | Assamese | es   |
| Malayalam | ml   | Odia     | or   |

### Supporting Domain
The universe in which the Streaming STT API is used for transcribing the speech. 

| Domain Name | SDK Constants                       |
|-------------|-------------------------------------|
| generic     | `RevSdkConstants.SttDomain.GENERIC` |
| bfsi        | `RevSdkConstants.SttDomain.BFSI`    |
| ecomm       | `RevSdkConstants.SttDomain.ECOMM`   |


To understand the `Domains` better please refer to [Supporting Domains](https://docs.reverieinc.com/reference/speech-to-text-streaming-api#supporting-domains)

## Integrate the SDK in your application.

[Refer this document ](https://github.com/reverieinc/Reverie-Language-API-SDK-Android/blob/main/README.md)


Add the following code bases in module level `build.gradle`, so the library can easily access the credentials. 
```
defaultConfig {
    buildConfigField "String", "REV_API_KEY", "\"${properties.getProperty("API-KEY")}\""
    buildConfigField "String", "REV_APP_ID", "\"${properties.getProperty("APP-ID")}\""
}
```
Kotlin DSL
```
import java.util.Properties

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream()
android {

defaultConfig {
        buildConfigField("String", "REV_API_KEY", "\"${properties["API-KEY"]}\"")
        buildConfigField("String", "REV_APP_ID", "\"${properties["APP-ID"]}\"")
    }
}


```
### Prerequisites

Please make sure the following requirements are met for the audio before uploading:

|                |                                                                                                                                                       |
|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| No. of Channel | **1  (MONO)**                                                                                                                                         |
| Time Limit     | The requests are limited to audio data of 180 seconds (3 minutes) or less in duration. Note: The default audio length duration is set for 15 seconds. |
| Sampling Rate  | **16000Hz**                                                                                                                                           |                                                                                                                                      |                                                

### Optional Parameters
| Parameter | Type  | Description                               | Java code                       | Kotlin code                     |
|-----------|-------|-------------------------------------------|---------------------------------|---------------------------------|
| timeout   | float | [Description](#Description-of-Parameters) | `streamingSTT.setTimeout(0.5f)` | `streamingSTT.setTimeout(3.0f)` | 
| silence   | float | [Description](#Description-of-Parameters) | `streamingSTT.setSilence(0.3f)` | `streamingSTT.setSilence(2.0f)` |

#### Description of Parameters
1. **timeout**:The duration to keep a connection open between the application and the STT server.   
   Note: The default `"timeout = 15 seconds`", and the maximum time allowed = 180 seconds
2. **silence**:The time to determine when to end the connection automatically after detecting the silence after receiving the speech data.
   Example:Consider `"silence = 15 seconds"` i.e., On passing the speech for 60 seconds, and if you remain silent, the connection will be open for the next 15 seconds and then will automatically get disconnected.
   Note: The default silence= 1 second, and the maximum `"silence = 30 seconds"`.

### Optional Logging
For enhanced debugging and logging, you can enable verbose logs and additional debugging for the STT API:
- To get verbose logs of the SDK, set this:  
   `RevSdkConstants.VERBOSE = true;`
- For additional STT-API debugging, use this method:
   `streamingSTT.setApiDebug(true);`

To know more about Pre-requisites and audio Formats refer to [Supporting Audio Formats](https://docs.reverieinc.com/reference/speech-to-text-streaming-api/supporting-audio-format)
## SDK usage example:
- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)

### Kotlin based example implementation of the SDK:

1. Prepare the constructor: 
     ```kotlin 
     
           //Calling the Constructor of StreamingSDK 
             val streamingSTT = StreamingSTT(
                  context,
                  BuildConfig.REV_API_KEY,
                  BuildConfig.REV_APP_ID
              )
    ```
 
2. Implement the result listener: 
    ```kotlin 
    //Implement Callbacks to get results and other necessary details
    streamingSTT.setOnResultListener(object : StreamingSTTResultListener {

            override fun onResult(result: StreamingSTTResultData?) {
                //Log.e("response Activity", result.toString())
            }


            override fun onError(result: StreamingSTTErrorResponseData) {
                
            }

            override fun onConnectionSuccess(result: String) {
                //    Log.d("Stream_Connec", result)
            }

            override fun onRecordingStart(status: Boolean) {
                // Log.d("Streamstart Activity", status.toString())
            }

            override fun onRecordingEnd(status: Boolean) {
                //    Log.d("Streamstop Activity", status.toString())
            }


            override fun onRecordingData(data: ByteArray, amplitude: Int) {
                //   Log.d("Streamgoing Activity", data.toString())
            }


        })
    ```

3. Starting the recording. 
    ```kotlin
         //To Start the Recording
          streamingSTT.startRecognitions(targetLanguage, RevSdkConstants.SttDomain.GENERIC, RevSdkConstants.SttStreamingLog.TRUE);
    ```

### Java based example implementation of the SDK:

1. Prepare the constructor:
     ```java 
     
          //Calling the Constructor of StreamingSDK 
           StreamingSTT streamingSTT = new StreamingSTT(
                  context,
                  BuildConfig.API_KEY,//Api Key
                  BuildConfig.APP_ID//App Id
            );
              
    ```

2. Implement the listeners:
    ```java
        //Implementing Callbacks to get the SDK State
       StreamingSTTResultListener streamingSTTResultListener = new StreamingSTTResultListener() {
                    @Override
                    public void onResult(@Nullable StreamingSTTResultData result) {
                        Log.d("Result", result.getDisplay_text());
                    }

                    @Override
                    public void onError(@NonNull StreamingSTTErrorResponseData result) {
                        Log.d("Error", result.getError());
                    }

                    @Override
                    public void onConnectionSuccess(@NonNull String result) {

                    }

                    @Override
                    public void onRecordingStart(boolean status) {

                    }

                    @Override
                    public void onRecordingEnd(boolean status) {

                    }

                    @Override
                    public void onRecordingData(@NonNull byte[] data, int amplitude) {

                    }
                };


    ```  
 
3. Starting the recording. 
    ```java
          //To Start the Recording
          streamingSTT.startRecognitions(targetLanguage, RevSdkConstants.SttDomain.GENERIC, RevSdkConstants.SttStreamingLog.TRUE);
    ```
### Necessary Permissions
Following permissions are required for the StreamingSDK
```manifest
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### More Information
https://docs.reverieinc.com/reference/speech-to-text-streaming-api
        
