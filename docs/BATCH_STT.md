# Speech to Text | BatchSTT
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

The Speech-to-Text accurately converts speech into text using an API powered by Reverie's AI technology. The solution will transcribe the speech in real-time of various Indian languages and audio formats.
The solution is a fully managed and continually trained solution, which leverages machine learning to combine knowledge of grammar, language structure, and the composition of audio and voice signals to accurately transcribe the speech.
> **Note: On the basis of the selected [domain](#supporting-domain) the result will vary**
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
| Marathi   | mr   | Assamese | as   |
| Malayalam | ml   | Odia     | or   |

### Supporting Domain
The universe in which the Streaming STT API is used for transcribing the speech.

| Domain Name | SDK Constants                       |
|-------------|-------------------------------------|
| generic     | `RevSdkConstants.SttDomain.GENERIC` |
| bfsi        | `RevSdkConstants.SttDomain.BSFI`    |

To understand the `Domains` better please refer to [Supporting Domains](https://docs.reverieinc.com/speech-to-text-file-api#supporting-domains)

## Integrate the SDK in your application.

Add Reverie's authorized API key and APP-ID in the `local.properties` file.
```
API-KEY=VALID-API-KEY
APP-ID=VALID-APP-ID
```

Make sure that you have added the latest dependency in the module level `build.gradle` file.

```
dependencies {
         implementation 'com.github.reverieinc:Reverie-Language-API-SDK-Android:1.0'
}
```

### Prerequisites

Please make sure the following requirements are met for the audio before uploading:

|                |                                                                        |
|----------------|------------------------------------------------------------------------|
| No. of Channel | **1  (MONO)**                                                          |
| Time Limit     | Audio file length should be equal or less than 300 seconds (5 minutes) |
| Sampling Rate  | **16000Hz**                                                            |

> To explore more about other audio formats please refer to [Supporting Audio Formats](https://docs.reverieinc.com/speech-to-text-file-api/supporting-audio-format)


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


### Mandatory Parameters
| Parameters | Type  | Description                                        |
|------------|-------|----------------------------------------------------|
| file       | file  | An audio file for which the transcript is desired. |

### Optional Parameters
| Parameters | Type   | Description                               | Java sample code                           | Kotlin sample code                    |
|------------|--------|-------------------------------------------|--------------------------------------------|---------------------------------------|
| format     | String | [Description](#Description-of-parameters) | `batchSTTInstance.setFormat("16k_int16");` | `batchSTTInstance.format="16k_int16"` |


#### Description of parameters
1. **format** : It is only required for the first API i.e Upload File API
   Note - By default, the format = 16k_int16. (WAV, Signed 16 bit, 16,000 or 16K Hz).To know more about formats check https://docs.reverieinc.com/speech-to-text-batch-api/supporting-audio-format


### Optional Logging
For enhanced debugging and logging, you can enable verbose logs for the SDK:
- To get verbose logs of the SDK, set this:  
  `RevSdkConstants.VERBOSE = true;`


## SDK usage example:

- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)

### Kotlin based example implementation of the SDK:

1. Preparing the constructor.
   ```kotlin 
    
    //Calling the Constructor of BATCHSTTSDK 
     batchSTTInstance = BatchSTT(
               BuildConfig.REV_API_KEY,
               BuildConfig.REV_APP_ID,
               listener,
               applicationContext
           )
   
   ```
2. Implement the listeners.
   ```kotlin 
       val listener: BatchSTTResultListener = object : BatchSTTResultListener {
            override fun onFailure(error: BatchSTTErrorResponseData) {
               Log.d(TAG, "OnFailure"+ error.message)                
            }

            override fun onStatusSuccess(status: BatchStatusResponse) { 
                Log.d(TAG, "onStatusSuccess"+ status.status)              
            }

            override fun onTranscriptSuccess(response: BatchTranscriptResponse) {
                Log.d(TAG, "onSttSuccess"+ response.result.transcript)               
            }

            override fun onUploadSuccess(response: BatchUploadResponse) {
                Log.d(TAG, "onUploadSuccess"+ response.message)
            }

        }          
         
   ```
3. Start the file uploading process:
   ```kotlin
         //selecting domain as Default
         domain: Int = RevSdkConstants.SttDomain.GENERIC
         
         //To Start the Uploading
          batchSTTInstance.uploadAudio(
           path,               //path of the file
           domain,             //Domain
           RevSdkConstants.Language.Hindi       //Target LANGUAGE
         )
   
   
   ```
4. To get the status of whether the conversion process is complete:
   ``` kotlin
          // checking the status of the uploaded file in terms of getting the transcript
          batchSTTInstance.checkStatus(jobId) // you can get the jobid from onUploadSuccess
   ```
5. To get the transcript after the conversion is complete:
  ``` kotlin
        batchSTTInstance.getTanscript(jobId)
  ```
> Note: Only if the status code is 000, you can get the transcript.

### Java based example implementation of the SDK:
1. Preparing the constructor.
   ```java 
    
    //Calling the Constructor of FILESTTSDK 
     BatchSTT batchSTTInstance = new BatchSTT(
               BuildConfig.REV_API_KEY,
               BuildConfig.REV_APP_ID,
               listeners,
               applicationContext
           )
   
   ```
2. Implement the listeners.
   ```java 
       //Implement Callbacks to get results and other necessary details
      BatchSTTResultListener batch=new BatchSTTResultListener() {
                   @Override
                   public void onTranscriptSuccess(@NonNull BatchTranscriptResponse response) {
                       Log.d("OnSuccess",response.getResult().getTranscript());
                   }

                   @Override
                   public void onFailure(@NonNull BatchSTTErrorResponseData error) {
                        Log.d("OnFailure",error.getMessage());
                   }

                   @Override
                   public void onStatusSuccess(@NonNull BatchStatusResponse status) {
                        Log.d("OnStatusSucess",status.getStatus());
                   }

                   @Override
                   public void onUploadSuccess(@NonNull BatchUploadResponse response) {
                       Log.d("OnUploadSuccess",response.getJobid());
                   }
               };    
         
   ```

3. Start the file uploading process:
   ```java

         //selecting domain as Default
         int domain = RevSdkConstants.SttDomain.GENERIC
         //To Start the Uploading
         batchSTTInstance.uploadAudio(
           path,               //path of the file
           domain,             //Domain
           RevSdkConstants.Language.Hindi     //Target LANGUAGE
         )
   
   ```
4. To get the status of whether the conversion process is complete:
   ```java

        //checking the status of the uploaded file in terms of getting the transcript 
        batchSTTInstance.checkStatus(jobid); //you can get the jobid from onUploadSuccess
   ```
5. To get the transcript after the conversion is complete:
   ``` java
        batchSTTInstance.getTanscript(jobId)
   ```

> Note: Only if the status code is 000, you can get the transcript.Note: Only if the status code is 000, you can get the transcript.

### Necessary Permissions

Following permissions are required for the BatchStt SDK
```manifest
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

### More Information
https://docs.reverieinc.com/speech-to-text-batch-api
