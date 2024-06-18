# Text to Speech
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

Reverie's TTS (Text-to-Speech) is a solution that turns text into lifelike speech, allowing you to create applications that talk in multiple Indic languages and build comprehensive speech-enabled products. 
The Reverie TTS service will offer neural Text-to-Speech voices, delivering innovative enhancements in speech quality through state-of-the-art machine learning approaches. You can select the ideal voice and tone to build the natural and human-like speech-enabled applications in the market to enable the interactive customer experience.
**Note: On the basis of the selected [domain](#supporting-domains) the result will vary**



### Supporting Speaker Codes
Here are some sample valid speaker’s code with respect to the language:

| Language       | Female                     | Male                                |
|----------------|----------------------------|-------------------------------------|
| Hindi          | `hi_female`, `hi_female2`  | `hi_male`, `hi_male_2`, `hi_male_3` |
| Gujarati       | `gu_female`                | `gu_male`                           |
| Tamil          | `ta_female`                | `ta_male`                           |
| Telugu         | `te_female`, `te_female_2` | `te_male`                           |
| Marathi        | `mr_female`, `mr_female_2` | `mr_male`, `mr_male_2`              |
| Malayalam      | `ml_female`                | `ml_male`                           |
| Bengali        | `bn_female`                | `bn_male`                           |
| Kannada        | `kn_female`                | `kn_male`                           |
| Punjabi        | `pa_female`                | `pa_male`                           |
| Indian English | `en_female`                | `en_male`, `en_male_2`              |
| Assamese       | `as_female`                | `as_male`                           |
| Odia           | `or_female`                | `or_male`                           |

> Please refer to [Supporting Speaker Code](https://docs.reverieinc.com/reference/text-to-speech-api/supporting-speaker-code) for updated valid speaker codes.

### Key Features
Reverie TTS API delivers remarkable robust features that effectively serve consumers in their native Indian language:
- Faster than Real-time Speech Synthesis:The TTS API will swiftly synthesize the speech output, consuming less time than the time consumed to speak in real-time. This enables real-time user experience for your users using the application.
- Customize the Speech Model:Train the text-to-speech solution to suit your requirements. The Reverie TTS will support lexicons and SSML tags, which allow you to manage the speech aspects like volume, pitch, speed rate, the pronunciation of words with context, etc.
- Text and SSML Support:Customize your speech with SSML tags that allow you to add pauses, numbers, date and time formatting, and other pronunciation instructions.
- High-Quality & Accurate Pronunciation:Attune your speech with SSML tags that allow you to add pauses, numbers, date and time formatting, and other pronunciation instructions, enabling you to deliver an accurate and high-quality voice-output.
- Optimize Your Speech Output: You can choose from various sampling rates to optimize bandwidth and audio quality for your application. The Reverie TTS supports WAV, OGG, MP3, FLAC, Ogg Opus, and PCM audio formats with sampling rates ranging from 8kHz, 16kHz, 22.05kHz, 24kHz, 44.1kHz, and 48kHz.
- Branded Custom Voices:We work with you on your voice requirements, select voice characteristics, and create and test your voice until it's ready to stand out of the crowd.

## Benefits of Reverie TTS
Reverie’s TTS builds a comprehensive speech application as it is empowered with: 
- Extensive depository of lifelike voices
- AI-optimized text processing
- Dedicated support for multiple Indic languages
- Allows customization to create unique voice personas

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

### Mandatory Parameters
| Parameters | Type n        | Description                                                                                                                                        |
|------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| text       | string/ array | The plain text or SSML input to synthesize an audio output.If you want to follow W3 standards, the ssml field must be used and not the text field. |

### Optional Parameters
| Parameters  | Type            | Description                               | Java code                      | Kotlin code                     |
|-------------|-----------------|-------------------------------------------|--------------------------------|---------------------------------|
| speed       | float (seconds) | [Description](#Desciption-of-parameters)  | `textToSpeech.setSpeed(1.5f);`   | `textToSpeechObj.speed=2f`      |  
| pitch       | float (seconds) | [Description](#Desciption-of-parameters)  | `textToSpeech.setPitch(1f);`     | `textToSpeechObj.pitch=1f`      | 
| sample_rate | integer         | [Description](#Desciption-of-parameters)  | `textToSpeech.setSampleRate(1)`  | `textToSpeechObj.format="WAV"`  |
| format      | string          | [Description](#Desciption-of-parameters)  | `textToSpeech.setFormat("WAV");` | `textToSpeechObj.sampleRate=2`  |

### Description of parameters
1. **speed**:The speech rate of the audio file. Allows values: from 0.5 (slowest speed rate) up to 1.5 (fastest speed rate).
   Note: By default, `"speed = 1"` (normal speed).
2. **pitch**:Speaking pitch, in the range Allows values: from -3 up tp 3. 3 indicates an increase of 3 semitones from the original pitch. -3 indicates a decrease of 3 semitones from the original pitch.
3. **sample_rate**:The sampling rate (in hertz) to synthesize the audio output Note: By default, the `"sample_rate = 22050 Hz (22.05kHz)"`. To know more about sample_rates check https://docs.reverieinc.com/reference/text-to-speech-api/supporting-sampling-rate
4. **format**:The speech audio format to generate the audio file Note: By default, the `"format = WAV"`. To know more about formats check https://docs.reverieinc.com/reference/text-to-speech-api/supporting-audio-format


### Optional Logging
For enhanced debugging and logging, you can enable verbose logs for the SDK:
- To get verbose logs of the SDK, set this:  
  `RevSdkConstants.VERBOSE = true;`

## SDK usage example:
- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)

### Kotlin based example implementation of the SDK:

1. Prepare the constructor:
   ```kotlin 
   
       val textToSpeech = TextToSpeech(
              BuildConfig.REV_API_KEY,
              BuildConfig.REV_APP_ID,
              context,
              listener
          )
  
   ```
 
2. Implementing the listener: 
   ```kotlin 
       val listener: TextToSpeechResultListener = object : TextToSpeechResultListener {
            override fun onSuccess(file: TTSAudioData) {
                // Play the file
                val filepath = file.wavfile.path
                val mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer.setDataSource(filepath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()

                    mediaPlayer.setOnCompletionListener {
                        mediaPlayer.release()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                }


            }
            override fun onFailure(error: TTSErrorResponse) {
                Log.d("Error", error.message)
            }

            override fun onError(res: TTSErrorResponse) {
                Log.d("Response", res.message)
            }
        }
   ```

3. Provide the input to speak: 
   ```kotlin
       textToSpeech.speak(
                    inputString,
                    sourceLanguage
                )
   ```
### Java based example implementation of the SDK:

1. Preparing the constructor.
   ```java 
  
       TextToSpeech textToSpeech = new TextToSpeech(
              BuildConfig.REV_API_KEY,
              BuildConfig.REV_APP_ID,
              context,
              listener
          );  
   ```

2. Implementing the listener:
     ```java 
   
        TextToSpeechResultListener listener = new TextToSpeechResultListener() {
            @Override
            public void onSuccess(TTSAudioData file) {
                String filepath = file.getWavfile().getPath();
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(filepath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.release();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(TTSErrorResponse error) {
                Log.d("Error", error.getMessage());
            }

            @Override
            public void onError(TTSErrorResponse res) {
                Log.d("Response", res.getMessage());
            }
        };
  
      ``` 
    
3. Provide the input to speak:
   ```java
        textToSpeech.speak(inputString, RevSdkConstants.TTSSpeaker.ENGLISH_FEMALE);
   ```
### Necessary Permissions
Following permissions are required for the TextToSpeech SDK
```manifest
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
### More Information
https://docs.reverieinc.com/reference/text-to-speech-api


