# Language Identification
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

This Api is used to detect or identify the language of the source content

# Supporting Language

| Languages in native script | Language in native script | 
|----------------------------|---------------------------|
| Assamese                   | Bengali                   | 
| Bengali                    | Gujarati                  | 
| English                    | Hindi                     | 
| Gujarati                   | Kannada                   | 
| Hindi                      | Marathi                   |   
| Malayalam                  | Telugu                    | 
| Marathi                    |                           |
| Odia                       |                           |
| Punjabi                    |                           |
| Tamil                      |                           |
| Telugu                     |                           |

### Integrate the SDK in your application

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
| Parameters | Type    | Description                        |
|------------|---------|------------------------------------|
| text       | String  | Input text for language detection. |

### Optional Parameters
| Parameters | Type | Description                               | Java Code                   | Kotlin Code            |  
|------------|------|-------------------------------------------|-----------------------------|------------------------|
| max_length | int  | [Description](#Description-of-Parameters) | `instance.setMaxlength(2);` | `instance.maxlength=2` |

### Description of Parameters
1. **max_length**:Length of the string to be processed for tokenization. It should be a number in the power of 2(i.e, 16 = 2^4, 32 = 2^5 ..). Max value could be 512.


### Optional Logging
For enhanced debugging and logging, you can enable verbose logs for the SDK:
- To get verbose logs of the SDK, set this:  
  `RevSdkConstants.VERBOSE = true;`

### SDK usage example :
- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)

### Kotlin based example implementation of the SDK:

1. Preparing the constructor.
     ```kotlin 
     
            val detector = TextLanguageDetector(
                BuildConfig.REV_API_KEY,//Api Key
                BuildConfig.REV_APP_ID,//App Id
                CONTEXT, // Context,
                callback
            )
 

      ```
2. Implementing the listeners for handling the response:
    ```kotlin 
        val callback:TextLanguageDetectionListener=object:TextLanguageDetectionListener
            {
                override fun onFailure(error: TextLanguageDetectionError) {
                    textType?.text=error.response
                }

                override fun onSuccess(response: TextLanguageDetectionResult) {
                    textType?.text=response.language
                }

            }
                
    ```
   
3. Provide the input text for identification
    ```kotlin
           detector.identifyLanguage(enterText?.text.toString())
    ````

### Java based example implementation of the SDK:

1. Preparing the constructor.
     ```java 
     
            TextLanguageDetector detector = new TextLanguageDetector(
                    BuildConfig.REV_API_KEY,
                    BuildConfig.REV_APP_ID,
                    context,
                    detectionListener);
 

      ```
2. Implementing the listeners for handling the response:
    ```java 
        TextLanguageDetectionListener detectionListener = new TextLanguageDetectionListener() {
                @Override
                public void onSuccess(@NonNull TextLanguageDetectionResult response) {
                    Log.d("OnSuccess", response.getLanguage());
                }

                @Override
                public void onFailure(@NonNull TextLanguageDetectionError error) {
                    Log.d("OnFailure", error.component1());
                }
            };
                
    ```

3. Provide the input text for identification
    ```java
           detector.identifyLanguage("What");//pass the input text 
    ````


 ### Necessary Permissions
Following permissions are required for the TranslationSDK
```manifest
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
### More Information
https://docs.reverieinc.com/language-identification-api 
