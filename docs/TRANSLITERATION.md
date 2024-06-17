# Transliteration
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/Reverie-Language-API-SDK-Android.svg)](https://jitpack.io/#reverieinc/Reverie-Language-API-SDK-Android)

Transliteration is the process of converting texts from one script to another based on phonetic similarity. Here, the text is displayed in alphabets of different languages, but the pronunciation, grammar, and sense of the original version remain intact in these new characters. 
Transliteration is involved while converting the names, addresses, titles, and more into Indian languages as localization is important without changing meaning. For a better understanding, refer to the below examples:
> **Note: On the basis of the selected [domain](#supporting-domains) the result will vary**

| Type        | English Script | Hindi Script   |
|-------------|----------------|----------------|
| City Name   | New Delhi      | न्यू दिल्ली    |
| Brand       | Bata           | बाटा           |
| Person Name | AR Rahaman     | ए आर रहमान     |

### Supporting Languages
Transliteration API supports 22 Indic languages, including Rare languages. It converts scripts from English to 22 Indic languages and 12 Indic languages to English. It also transliterates scripts from one regional language to another (for example: Hindi to Tamil). 

| Language Name | Language Code | Language Name | Language Code |
|---------------|---------------|---------------|---------------|
| Hindi         | hi            | Odia          | or            |
| Assamese      | as            | Punjabi       | pa            |
| Bengali       | bn            | Tamil         | ta            |
| Kannada       | kn            | Telugu        | te            |
| Malayalam     | ml            | English       | en            |
| Marathi       | mr            | Urdu          | ur            |
| Konkani       | kom           | Sindhi        | sd            |
| Dogri         | doi           | Bodo          | brx           |
| Kashmiri      | ks            | Maithili      | mai           |
| Manipuri      | mni           | Sanskrit      | sa            |
| Santhali      | sat           | Gujarati      | gu            |



### Supporting Domains


| Domain Name           | SDK Constant                                                    |
|-----------------------|-----------------------------------------------------------------| 
| Default               | `RevSdkConstants.TransliterationDomain.DEFAULT`                 |
| Names                 | `RevSdkConstants.TransliterationDomain.NAMES`                   |
| OnlyEnglish           | `RevSdkConstants.TransliterationDomain.ONLY_ENGLISH`            |
| Cricketers            | `RevSdkConstants.TransliterationDomain.CRICKETERS`              |
| Banking               | `RevSdkConstants.TransliterationDomain.BANKING`                 |
| Brands                | `RevSdkConstants.TransliterationDomain.BRANDS`                  |
| Addresses             | `RevSdkConstants.TransliterationDomain.ADDRESSES`               |
| NumberInWords         | `RevSdkConstants.TransliterationDomain.NUMBER_IN_WORDS`         |
| Food                  | `RevSdkConstants.TransliterationDomain.FOOD`                    |
| Media & Entertainment | `RevSdkConstants.TransliterationDomain.MEDIA_AND_ENTERTAINMENT` |
| Grocery               | `RevSdkConstants.TransliterationDomain.GROCERY`                 |
| Healthcare            | `RevSdkConstants.TransliterationDomain.HEALTH_CARE`             |
| Enterprise            | `RevSdkConstants.TransliterationDomain.ENTERPRISE`              |



> To understand the `Domains` better please refer to [Supporting Domains](https://docs.reverieinc.com/reference/transliteration-api#supporting-domains)



### Integrate the SDK in your application.

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

## SDK usage example:
- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)

### Mandatory Parameters
| Parameters | Type           | Description                             |
|------------|----------------|-----------------------------------------|
| data       | list of String | List of input text for transliteration. |

### Optional Parameters
| Parameters             | Type    | Description                               | Java sample code                                  | Kotlin sample code                               |
|------------------------|---------|-------------------------------------------|---------------------------------------------------|--------------------------------------------------|
| isBulk                 | boolean | [Description](#Description-of-Parameters) | `transliteration.setBulk(true);`                  | `transliteration.isBulk=true`                    |
| noOfSuggestions        | integer | [Description](#Description-of-Parameters) | `transliteration.setNoOfSuggestions(4);`          | `transliteration.noOfSuggestions=2`              |
| abbreviate             | boolean | [Description](#Description-of-Parameters) | `transliteration.setAbbreviate(true);`            | `transliterationObj.abbreviate=true`             |
| convertNumber          | boolean | [Description](#Description-of-Parameters) | `transliteration.setConvertNumber(true)`          | `transliteration.convertNumber=true`             |
| ignoreTaggedEntities   | boolean | [Description](#Description-of-Parameters) | `transliteration.setIgnoreTaggedEntities(true);`  | `transliterationObj.ignoreTaggedEntities=true`   |
| convertRoman           | boolean | [Description](#Description-of-Parameters) | `transliteration.setConvertRoman(true);`          | `transliterationObj.convertNumber=true`          |
| convertOrdinal         | boolean | [Description](#Description-of-Parameters) | `transliteration.setConvertOrdinal(true);`        | `transliterationObj.convertOrdinal=true`         |
| abbreviationWithoutDot | boolean | [Description](#Description-of-Parameters) | `transliteration.setAbbreviationWithoutDot(true)` | `transliterationObj.abbreviationWithoutDot=true` |

### Description of Parameters 
1. **isBulk**: Specify whether the API should initially search in the Exception DB to transliterate the input text. 
   Note: By default, the `isBulk= true` and will not search in the Exception DB.
2. **noOfSuggestions**: Mention the number of transliteration suggestions the API should return for the input text.
3. **abbreviate**: The abbreviate will Validate whether any Abbreviations/ Acronyms are passed in the input text and will transliterate it accurately. 
   Note - By default, the `abbreviate = true` 
   Note - if the value is `false`, then the API will consider the abbreviation as a word and will transliterate to the nearest available word. 
   Note - In the input text, pass the abbreviations in the upper case.
4. **convertNumber**: Specify whether to convert the numbers in the input text to the target language script. 
   Note - By default, the `convertNumber = false`
5. **ignoreTaggedEntities**: Specify whether you want to retain the entities like email ID and URL in the input script.
6. **convertRoman**: This is used for transliterating Roman numbers to English numbers. 
   Note - Default `value = false` For example - If the user types sector V in English - The transliteration would be - सेक्टर 5 in Hindi. Block II will transliterated as ब्लॉक 2. 
   Note - To translate numbers in the Indian language use convertNumber as mentioned in the table.
7. **convertOrdinal**: This is used for transliterating ordinal values to English numbers. 
   Note - Default `value = false` For example - If the user types 15th Main in English, - The transliteration would be - 15 मेन in Hindi.
8. **abbreviationWithoutDot**: This is used to produce the abbreviation output without a dot. 
   Note - Default `value = false` For example- If a user wants an abbreviation output without a dot and is given SMS as an input then the result would be - एसएमएस


### Optional Logging
For enhanced debugging and logging, you can enable verbose logs for the SDK:
- To get verbose logs of the SDK, set this:  
  `RevSdkConstants.VERBOSE = true;`


### Kotlin based example implementation of the SDK:

1. Prepare the constructor: 
     ```kotlin 
       //selecting domain as Default
         val domain: Int = RevSdkConstants.TransliterationDomain.DEFAULT
             
         //Calling the constructor of Transliteration
         val transliterationObj = Transliteration(
                    BuildConfig.REV_API_KEY,
                    BuildConfig.REV_APP_ID, 
                    domain,
                    listener,
                    context
                )
    ```

2. Implement the listeners for getting the results. 
    ```kotlin 
        val listener = object : TransliterationResultListener {
            override fun onSuccess(response: TransliterationData) {
                Log.d("Response",response.responseList[0].outString)
            }
    
            override fun onFailure(error: TransliterationError) {
                Log.d("Error",error.message)
            }
    
        }
               
    ```

3. Initiate Transliteration object:

    ```kotlin
        transliterationObj.transliterate(
                data = sentences, //List of Sentences
                RevSdkConstants.Language.ENGLISH, //Source Language
                RevSdkConstants.Language.HINDI //Target Language
                )
    
    
    
    ```

### Java based example implementation of the SDK:

1. Prepare the constructor:
     ```java 
         //selecting domain as Default
         int domain = RevSdkConstants.TransliterationDomain.DEFAULT;
         
         //Calling the constructor of Transliteration
         Transliteration transliterationObj = new Transliteration(
                    BuildConfig.REV_API_KEY,
                    BuildConfig.REV_APP_ID, 
                    domain,
                    listener,
                    CONTEXT
                );
    ```

2. Implement the listeners for getting the results.
    ```java 
        TransliterationResultListener listener =new TransliterationResultListener {
            override fun onSuccess(response: TransliterationData) {
                Log.d("Response",response.getResponseList().get(0).getOutString())
            }
    
            override fun onFailure(error: TransliterationError) {
                Log.d("Error",error.getMessage())
            }
    
        };
               
    ```

3. Initiate Transliteration object:

    ```java
       transliterationObj.transliterate(
                    data = sentences, //List of Sentences
                    RevSdkConstants.Language.ENGLISH, //Source Language
                    RevSdkConstants.Language.HINDI //Target Language
                );
    
    ```
### Necessary Permissions
Following permissions are required for the Transliteration SDK

```manifest
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>

```
### More Information
https://docs.reverieinc.com/reference/transliteration-api




