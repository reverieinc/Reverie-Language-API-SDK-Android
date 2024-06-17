package com.reverie.sdk.utilities.constants

const val REVERIE_BASE_URL = "https://revapi.reverieinc.com/"
const val REVERIE_BASE_URL_BATCH = "https://revapi.reverieinc.com/upload"
const val REVERIE_BASE_URL_BATCH_STATUS = "https://revapi.reverieinc.com/status?job_id="

const val REVERIE_BASE_URL_BATCH_TRANSCRIPT = "https://revapi.reverieinc.com/transcript?job_id="
const val REV_STT_STREAM_URL = "wss://revapi.reverieinc.com/stream?"

const val HEADER_API_KEY = "REV-API-KEY"
const val HEADER_APP_ID = "REV-APP-ID"
const val HEADER_APPNAME = "REV-APPNAME"
const val HEADER_DOMAIN = "domain"
const val HEADER_SPEAKER = "speaker"
const val HEADER_SOURCE_LANGUAGE = "src_lang"
const val HEADER_TARGET_LANGUAGE = "tgt_lang"

//App names
const val TRANSLATION_APP_NAME = "localization"
const val TRANSLITERATION_APP_NAME = "transliteration"
const val TTS_APP_NAME = "tts"
const val STT_APP_NAME = "stt_file"
const val STT_BATCH = "stt_batch"
const val LANGUGE_IDENTIFICATION = "lang_id_text"
const val BATCH_FAILED_001 = "001"
const val BATCH_FAILED_002 = "002"
const val BATCH_FAILED_005 = "005"
const val BATCH_FAILED_009 = "999"
const val BATCH_RECALL_003 = "003"
const val BATCH_RECALL_004 = "004"
const val BATCH_SUCCESS = "000"


//Times constants of File STT
const val CONNECT_TIMEOUT_TIME = 15000
const val READ_TIMEOUT_TIME = 30000
const val WRITE_TIMEOUT_TIME: Long = 3000


//Warning messages
const val WARNING_MISSING_MANIFEST =
    "Please ensure the following permissions are declared in the manifest:\n"
const val WARNING_PERMISSIONS_GRANT_REQUIRED =
    "Please grant all the required permissions for the application."
const val WARNING_NO_INTERNET = "Please ensure you have an active internet connection."
const val INVALID_JOBID = "Invalid Jobid"
const val DEFAULT_TIME_INTERVAL: Long = 2000

//public class Languages() {
//    companion object {
//        const val HINDI = "hi"
//        const val ASSAMESE = "as"
//        const val BENGALI = "bn"
//        const val GUJARATI = "gu"
//        const val KANNADA = "kn"
//        const val MALAYALAM = "ml"
//        const val MARATHI = "mr"
//        const val ODIA = "or"
//        const val PUNJABI = "pa"
//        const val TAMIL = "ta"
//        const val TELUGU = "te"
//        const val ENGLISH = "en"
//    }
//}

//public class Debug() {
//    companion object {
//        var DEBUG = false
//    }
//}

object RevSdkConstants {
    var VERBOSE = true

    object Language {
        const val HINDI = "hi"
        const val ASSAMESE = "as"
        const val BENGALI = "bn"
        const val GUJARATI = "gu"
        const val KANNADA = "kn"
        const val MALAYALAM = "ml"
        const val MARATHI = "mr"
        const val ODIA = "or"
        const val PUNJABI = "pa"
        const val TAMIL = "ta"
        const val TELUGU = "te"
        const val ENGLISH = "en"
    }

    object TranslationDomain {
        const val GENERAL = 1
        const val TRAVEL = 2
        const val ECOMMERCE = 3
        const val MUSIC = 4
        const val BANKING = 5
        const val GROCERY = 6
        const val EDUCATION = 7
        const val MEDICAL = 8

    }

    object TransliterationDomain {
        const val DEFAULT = 1
        const val NAMES = 2
        const val ONLY_ENGLISH = 4
        const val CRICKETERS = 5
        const val BANKING = 6
        const val BRANDS = 7
        const val ADDRESSES = 9
        const val NUMBER_IN_WORDS = 10
        const val FOOD = 22
        const val MEDIA_AND_ENTERTAINMENT = 25
        const val GROCERY = 26
        const val HEALTH_CARE = 27
        const val ENTERPRISE = 28
    }

    object SttDomain {
        const val GENERIC = "generic"
        const val BFSI = "bfsi"
        const val ECOMM = "ecomm"
    }

    object TTSSpeaker {
        const val ENGLISH_FEMALE = "en_female"
        const val HINDI_FEMALE = "hi_female"
        const val HINDI_MALE = "hi_male"
        const val ODIA_MALE = "or_male"
        const val ODIA_FEMALE = "or_female"
        const val BENGALI_MALE = "bn_male"
        const val BENGALI_FEMALE = "bn_female"
        const val KANNADA_MALE = "kn_male"
        const val KANNADA_FEMALE = "kn_female"
        const val MALAYALAM_MALE = "ml_male"
        const val MALAYALAM_FEMALE = "ml_female"
        const val TAMIL_MALE = "ta_male"
        const val TAMIL_FEMALE = "ta_female"
        const val TELUGU_MALE = "te_male"
        const val TELUGU_FEMALE = "te_female"
        const val GUJARATI_MALE = "gu_male"
        const val GUJARATI_FEMALE = "gu_female"
        const val ASSAMESE_MALE = "as_male"
        const val ASSAMESE_FEMALE = "as_female"
        const val MARATHI_MALE = "mr_male"
        const val MARATHI_FEMALE = "mr_female"
        const val PUNJABI_MALE = "pa_male"
        const val PUNJABI_FEMALE = "pa_female"
    }

    object SttStreamingLog {
        const val TRUE = "true"
        const val FALSE = "false"
        const val NO_AUDIO = "no_audio"
        const val NO_TRANSCRIPT = "no_transcript"
    }
}


//public class TranslationDomain {
//    companion object {
//        const val GENERAL = 1
//        const val TRAVEL = 2
//        const val ECOMMERCE = 3
//        const val MUSIC = 4
//        const val BANKING = 5
//        const val GROCERY = 6
//        const val EDUCATION = 7
//        const val MEDICAL = 8
//
//    }
//
//
//}

//public class TransliterationDomain {
//    companion object {
//        const val DEFAULT = 1
//        const val NAMES = 2
//        const val ONLY_ENGLISH = 4
//        const val CRICKETERS = 5
//        const val BANKING = 6
//        const val BRANDS = 7
//        const val ADDRESSES = 9
//        const val NUMBER_IN_WORDS = 10
//        const val FOOD = 22
//        const val MEDIA_AND_ENTERTAINMENT = 25
//        const val GROCERY = 26
//        const val HEALTH_CARE = 27
//        const val ENTERPRISE = 28
//    }
//}

//public class SttDomain() {
//    companion object {
//        const val GENERIC = "generic"
//        const val BFSI = "bfsi"
//        const val ECOMM = "ecomm"
//    }
//}

//public class TTSSpeaker() {
//    companion object {
//        const val ENGLISH_FEMALE = "en_female"
//        const val HINDI_FEMALE = "hi_female"
//        const val HINDI_MALE = "hi_male"
//        const val ODIA_MALE = "or_male"
//        const val ODIA_FEMALE = "or_female"
//        const val BENGALI_MALE = "bn_male"
//        const val BENGALI_FEMALE = "bn_female"
//        const val KANNADA_MALE = "kn_male"
//        const val KANNADA_FEMALE = "kn_female"
//        const val MALAYALAM_MALE = "ml_male"
//        const val MALAYALAM_FEMALE = "ml_female"
//        const val TAMIL_MALE = "ta_male"
//        const val TAMIL_FEMALE = "ta_female"
//        const val TELUGU_MALE = "te_male"
//        const val TELUGU_FEMALE = "te_female"
//        const val GUJARATI_MALE = "gu_male"
//        const val GUJARATI_FEMALE = "gu_female"
//        const val ASSAMESE_MALE = "as_male"
//        const val ASSAMESE_FEMALE = "as_female"
//        const val MARATHI_MALE = "mr_male"
//        const val MARATHI_FEMALE = "mr_female"
//        const val PUNJABI_MALE = "pa_male"
//        const val PUNJABI_FEMALE = "pa_female"
//    }
//}