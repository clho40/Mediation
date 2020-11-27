package com.hmscl.huawei.ads.mediation_adapter_mopub.utils

class HuaweiAdsCustomEventDataKeys {
    companion object {
        const val KEY_EXTRA_APPLICATION_ID = "appid"
        const val AD_UNIT_ID_KEY = "adUnitID"
        const val TAG_FOR_CHILD_DIRECTED_KEY = "tagForChildDirectedTreatment"
        const val TAG_FOR_UNDER_AGE_OF_CONSENT_KEY = "tagForUnderAgeOfConsent"
        const val CONTENT_URL_KEY = "contentUrl"
        const val KEY_EXTRA_ORIENTATION_PREFERENCE = "orientation_preference"
        const val KEY_EXTRA_AD_CHOICES_PLACEMENT = "ad_choices_placement"
        const val KEY_EXPERIMENTAL_EXTRA_SWAP_MARGINS = "swap_margins"

        /* Custom Event Data - to be filled on MoPub Platform
        Banner, Interstitial, Rewarded
        {"appid":"111","adUnitID": "222", "tagForChildDirectedTreatment": "false",
        "tagForUnderAgeOfConsent": "false", "contentUrl" : "abc"
        }

        Native
        {
        "appid":"111","adUnitID": "222", "tagForChildDirectedTreatment": "false",
        "tagForUnderAgeOfConsent": "false", "contentUrl" : "abc",
        "orientation_preference": "aaa", "ad_choices_placement": "bbb", "swap_margins": "ccc"
        }
        */
    }
}