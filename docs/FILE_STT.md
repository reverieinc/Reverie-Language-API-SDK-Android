# Speech to Text | FileSTT
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

The Speech-to-Text accurately converts speech into text using an API powered by Reverie's AI technology. The solution will transcribe the speech in real-time of various Indian languages and audio formats.
The solution is a fully managed and continually trained solution, which leverages machine learning to combine knowledge of grammar, language structure, and the composition of audio and voice signals to accurately transcribe the speech.
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
| Parameters | Type | Description                               |
|------------|------|-------------------------------------------|
| audio_file | file | [Description](#Description-of-parameters) |

### Optional Parameters
| Parameters | Type   | Description                               | Java sample code                         | Kotlin sample code                   |
|------------|--------|-------------------------------------------|------------------------------------------|--------------------------------------|
| format     | String | [Description](#Description-of-parameters) | `fileSTTInstance.setFormat("16k_int16")` | `fileSTTInstance.format="16k_int16"` |
| logging    | String | [Description](#Description-of-parameters) | `fileSTTInstance.setLogging("no_audio")` | `fileSTTInstance.logging="no_audio"` |


#### Description of parameters
1. **audio_file** : Upload the audio file to obtain the transcript. Note - audio_file length should be equal to or less than 300 seconds (5 minutes).
2. **format** : Mention the audio sample rate and file format of the uploaded file By default, the `"format = 16k_int16"`. (WAV, Signed 16 bit, 16,000 or 16K Hz) for more formats check https://docs.reverieinc.com/speech-to-text-file-api/supporting-audio-format
3. **logging** : Default value=`"true"`. Possible values are:
   1. `"true"` : stores client’s audio and keeps transcript in logs.
   2. `"no_audio"` :  does not store client’s audio but keeps transcript in logs.
   3. `"no_transcript"` : does not keep transcript in logs but stores client’s audio.
   4. `"false"` : does not keep neither client’s audio nor transcript in log.


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
    
    //Calling the Constructor of File STT
     fileSTTInstance = FileSTT(
               BuildConfig.REV_API_KEY,
               BuildConfig.REV_APP_ID,
               listener,
               context
           )
   
   ```
2. Implement the listeners. 
   ```kotlin 
       //Implement Callbacks to get results and other necessary details
       val listener = object : FileSTTResultListener {
            override fun onSttSuccess(response: FileSTTResultData) {
                Log.d("Response",response.display_text)                
            }
           override fun onFailure(error: FileSTTErrorResponseData) {
                Log.d("Error Message", error.message)
           }
        }       
         
   ```
3. Start the file uploading process:
   ```kotlin
         //selecting domain as Default
         domain: Int = RevSdkConstants.SttDomain.GENERIC
         
         //To start the uploading
         fileSTTInstance.audioToText(
           path,               //path of the file
           domain,             //Domain
           RevSdkConstants.Language.Hindi       //Target LANGUAGE
         )
   
   ```
### Java based example implementation of the SDK:

1. Preparing the constructor.
   ```java 
    
    //Calling the Constructor of FILE STT
     FileSTT fileSTTInstance = new FileSTT(
               BuildConfig.REV_API_KEY,
               BuildConfig.REV_APP_ID,
               listeners,
               context
           )
   
   ```
2. Implement the listeners.
   ```java 
       //Implement Callbacks to get results and other necessary details
       FileSTTResultListener listeners = new  FileSTTResultListener() {
            override fun onSttSuccess(response: FileSTTResultData) {
                Log.d("Response",response.getDisplay_text())  
           
            }
           override fun onFailure(error: FileSTTErrorResponseData) {
                Log.d("Error Message", error.getMessage())
           }
        }       
         
   ```

3. Start the file uploading process:
   ```java
         //selecting domain as Default
         int domain = RevSdkConstants.SttDomain.GENERIC
         //To start the uploading
         fileSTTInstance.audioToText(
           path,               //path of the file
           domain,             //Domain
           RevSdkConstants.Language.Hindi     //Target LANGUAGE
         )
   
   ```
### Necessary Permissions
Following permissions are required for the SttFile SDK
```manifest
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

### More Information
https://docs.reverieinc.com/speech-to-text-file-api
