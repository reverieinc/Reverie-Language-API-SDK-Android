# Translation
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

Content Localization is the process of adapting the original content to a new target audience. The localization process requires more effort and skill than translation (e.g., changing measurement systems, adding or changing words to help a local reader). The translated text has to be simple, easy to understand, and capture subtle nuances and cultural references.
**Note: On the basis of the selected [domain](#supporting-domains) the result will vary**
The example below demonstrates how the Localization SDK  will analyze the content's domain & context and localize it:


| Source Content                            | Target Content                         |
|-------------------------------------------|----------------------------------------|
| Book 3 flight tickets to Delhi            | दिल्ली के लिए 3 फ्लाइट टिकट बुक करें   |
| I have read books written by Abdul Kalam. | मैंने अब्दुल कलाम की लिखी किताबें पढ़ी |

###  Supporting Languages




| Language  | Code | Language | Code |
|-----------|------|----------|------|
| Hindi     | hi   | Bengali  | bn   |
| Gujarati  | gu   | Kannada  | kn   |
| Tamil     | ta   | Punjabi  | pa   |
| Telugu    | te   | English  | en   |
| Marathi   | mr   | Assamese | as   |
| Malayalam | ml   | Odia     | or   |

### Supporting Domains

| Domain Name | SDK Constants                                  |
|-------------|------------------------------------------------|
| General     | `RevSdkConstants.TranslationDomains.GENERAL`   |
| Travel      | `RevSdkConstants.TranslationDomains.TRAVEL`    |
| Ecommerce   | `RevSdkConstants.TranslationDomains.ECOMMERCE` |
| Music       | `RevSdkConstants.TranslationDomains.MUSIC`     |
| Banking     | `RevSdkConstants.TranslationDomains.BANKING`   |
| Grocery     | `RevSdkConstants.TranslationDomains.GROCERY`   |
| Education   | `RevSdkConstants.TranslationDomains.EDUCATION` |
| Medical     | `RevSdkConstants.TranslationDomains.MEDICAL`   |

To understand the `Domains` better please refer to [Supporting Domains](https://docs.reverieinc.com/reference/localization-api#supporting-domains)

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
| Parameters | Description                          | Type           |
|------------|--------------------------------------|----------------|
| data       | List of input text for localization. | list of String |

### Optional Parameters
| Parameters        | Type             | Description                               | Java sample code                                                | Kotlin sample code                                                           |
|-------------------|------------------|-------------------------------------------|-----------------------------------------------------------------|------------------------------------------------------------------------------|
| dbLookupParam     | boolean          | [Description](#Description-of-parameters) | `translationInstance.setDbLookUpParam(true)`                    | `translationInstance.dbLookUpParam=true`                                     |
| segmentationParam | boolean          | [Description](#Description-of-parameters) | `translationInstance.setSegmentatioParam(true)`                 | `translationInstance.segmentationParam=true`                                 |
| nmtParam          | boolean          | [Description](#Description-of-parameters) | `translationInstance.setNmtParam(true)`                         | `translationInstance.nmtParam=true`                                          |
| builtInPreProc    | boolean          | [Description](#Description-of-parameters) | `translationInstance.setBuiltInPreProc(true)`                   | `translationInstance.builtInPreProc=true`                                    |
| debugMode         | boolean          | [Description](#Description-of-parameters) | `translationInstance.setDebugMode(true)`                        | `translationInstance.debugMode=true`                                         |
| usePrabandhak     | boolean          | [Description](#Description-of-parameters) | `translationInstance.setUsePrabandhak(true)`                    | `translationInstance.usePrabandhak=true`                                     |
| nmtMask           | boolean          | [Description](#Description-of-parameters) | `translationInstance.setNmtMask(true);`                         | `translationInstance.nmtMask=true`                                           |
| nmtMaskTerms      | array of content | [Description](#Description-of-parameters) | `translationInstance.setNmtMaskTerms("hello how are you","hi")` | `translationInstance.setNmtMaskTerms("Hello","This is for testing purpose")` |

### Description of parameters
1. dbLookupParam : The parameter will specify whether the application should refer to the Lookup DB or not. i.e., when thedbLookupParamis True, the system will initially refer to the Database to fetch contents. Note By default, the dbLookupParam= false.
2. segmentationParam : The parameter will specify whether the content should be segmented before localizing the input. Note By default, the segmentationParam= false
3. nmtParam : Specify whether the content localization process should use NMT technology or not. i.e., When thenmtParamvalue is True, the system will initially refer to the Lookup database to localize content. If the content is not available in the database, then the NMT is used for translation.Note By default, the nmtParam= false
4. builtInPreProc : Specify whether you want to pre-process the input and tag the strings according to patterns in the regex_pattern table Note By default, the builtInPreProc= false 
5. debugMode : The Debug parameter will provide log details about localized content.The details provided are the entity code, localization process type, and more.This information is useful to capture the log and analyze the system performance.Note By default, the debugMode= false
6. usePrabandhak : Specify whether you want to verify the unmoderated strings. Set usePrabandhak= true, then the API will send the unverified strings to the Prabandhak application for verification. Note: By default, the usePrabandhak= false. P.S: The usePrabandhak parameter is enabled only for the Reverie’s Manual Verification Service subscribers.
7. nmtMask : The feature to screen the non-dictionary words used in a sentence. In other words, the mask will indicate the words that should not be translated into the target language. Note - By default, the nmtMask = false Note - To set the nmtMask = true, it is mandatory the src_lang = en (English).
8. nmtMaskTerms : Determines the Words that are to be masked. Note - On defining values in the nmtMaskTerms, then automatically the nmtMask is set to true. Example - Masking a term -"nmtMaskTerms": ["Reverie Language Technologies"]Here, the API will mask the term Reverie Language Technologies, if found in the source content, and will transliterate the word.


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
     
        val translationInstance = Translation(
            BuildConfig.REV_API_KEY,//Api Key
            BuildConfig.REV_APP_ID,//App Id
            context // Context
        )
    ```

2. Implementing the listeners. 
    ```kotlin 
        val listener = object : TranslationResultListener {
            override fun onSuccess(response: TranslationData) {
                //listener of successfull translation       
                Log.d( response.responseList[0].outString ) 
            }
            
            override fun onFailure(error: TranslationError) {
                //listener of unsuccessfull translation
                Log.d("Error",error.message)
            }
        }
               
    ```

3. Initiating Translation. 
    ```kotlin
       //selecting domain as Default       
        val domain:Int = RevSdkConstants.TranslationDomain.GENERAL        
        
       translationInstance.translate(
                LIST_OF_SENTENCES,//list of Sentences needed to be translated
                RevSdkConstants.Language.ENGLISH, // the language code in which sentences are in
                RevSdkConstants.LANGUAGE.HINDI, // the language code in which sentences are required to be translated
                domain, //domain id
                listener
            )

    ```
### Java based example implementation of the SDK:

1. Preparing the constructor.
     ```java 
     
          Translation translation=new Translation( 
                BuildConfig.REV_API_KEY, //Api Key
                BuildConfig.REV_APP_ID, //App Id
                context //Context
          );
    ```
2. Implementing the listeners.
    ```java 
    TranslationResultListener listener=new TranslationResultListener()
        {
            @Override
            public void onSuccess(@NonNull TranslationData translationData) {
             //Your code when the request is successful
             //Log.d("Response",translationData.getResponseList().get(0).getOutString())
            }

            @Override
            public void onFailure(@NonNull TranslationError translationError) {
            // Your code when request fails
            //Log.d("Error",translationError.getMessage())
            }
     };
           
     ```

3. Initiating Translation.
    ```java
       //selecting domain as Default
        int domain =RevSdkConstants.TranslationDomains.GENERAL;        
        
        translation.translate(
            LIST_OF_SENTENCES, //sentences to be translated 
            RevSdkConstants.Language.ENGLISH, // the language code in which the entered sentence is
            RevSdkConstants.Language.HINDI, // the language code in which sentences are required to be translated
            domain,// domain id
            listener
        );
    
    ```
### Necessary Permissions
Following permissions are required for the TranslationSDK
```manifest
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>

```
### More Information
https://docs.reverieinc.com/reference/localization-api

