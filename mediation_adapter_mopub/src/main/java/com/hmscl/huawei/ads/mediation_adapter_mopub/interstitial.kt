package com.hmscl.huawei.ads.mediation_adapter_mopub

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsAdapterConfiguration
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsCustomEventDataKeys
import com.huawei.hms.ads.*
import com.mopub.common.LifecycleListener
import com.mopub.common.Preconditions
import com.mopub.common.logging.MoPubLog
import com.mopub.common.logging.MoPubLog.AdapterLogEvent
import com.mopub.mobileads.AdData
import com.mopub.mobileads.BaseAd
import com.mopub.mobileads.MoPubErrorCode

class interstitial : BaseAd() {
    val AD_UNIT_ID_KEY = HuaweiAdsCustomEventDataKeys.AD_UNIT_ID_KEY
    val CONTENT_URL_KEY = HuaweiAdsCustomEventDataKeys.CONTENT_URL_KEY
    val TAG_FOR_CHILD_DIRECTED_KEY = HuaweiAdsCustomEventDataKeys.TAG_FOR_CHILD_DIRECTED_KEY
    val TAG_FOR_UNDER_AGE_OF_CONSENT_KEY =
        HuaweiAdsCustomEventDataKeys.TAG_FOR_UNDER_AGE_OF_CONSENT_KEY
    private val ADAPTER_NAME: String = interstitial::class.java.getSimpleName()
    private var mHuaweiAdsAdapterConfiguration = HuaweiAdsAdapterConfiguration()
    private var mHuaweiInterstitialAd: InterstitialAd? = null
    private var mAdUnitId: String? = null

    override fun load(context: Context, adData: AdData) {
        Preconditions.checkNotNull(context)
        Preconditions.checkNotNull(adData)
        setAutomaticImpressionAndClickTracking(false)
        val extras = adData.extras
        if (extrasAreValid(extras)) {
            mAdUnitId = extras[AD_UNIT_ID_KEY]
            mHuaweiAdsAdapterConfiguration.setCachedInitializationParameters(context, extras)
        } else {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_NO_FILL.intCode,
                MoPubErrorCode.NETWORK_NO_FILL
            )
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL)
            }
            return
        }
        mHuaweiInterstitialAd = InterstitialAd(context)
        mHuaweiInterstitialAd!!.adListener = InterstitialAdListener()
        mHuaweiInterstitialAd!!.adId = mAdUnitId
        val builder = AdParam.Builder()
        builder.setRequestOrigin("MoPub")

        val contentUrl = extras[CONTENT_URL_KEY]
        if (!TextUtils.isEmpty(contentUrl)) {
            builder.setTargetingContentUrl(contentUrl)
        }
//        forwardNpaIfSet(builder)
        val requestConfigurationBuilder = HwAds.getRequestOptions().toBuilder()

        val childDirected = extras[TAG_FOR_CHILD_DIRECTED_KEY]
        if (childDirected != null) {
            if (java.lang.Boolean.parseBoolean(childDirected)) {
                requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_TRUE)
            } else {
                requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_FALSE)
            }
        } else {
            requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_UNSPECIFIED)
        }

        // Publishers may want to mark their requests to receive treatment for users in the
        // European Economic Area (EEA) under the age of consent.
        val underAgeOfConsent = extras[TAG_FOR_UNDER_AGE_OF_CONSENT_KEY]
        if (underAgeOfConsent != null) {
            if (java.lang.Boolean.parseBoolean(underAgeOfConsent)) {
                requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_TRUE)
            } else {
                requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_FALSE)
            }
        } else {
            requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_UNSPECIFIED)
        }
        val requestConfiguration = requestConfigurationBuilder.build()
        HwAds.setRequestOptions(requestConfiguration)
        val adRequest = builder.build()
        mHuaweiInterstitialAd!!.loadAd(adRequest)
        MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_ATTEMPTED, ADAPTER_NAME)
    }

    override fun show() {
        MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_ATTEMPTED, ADAPTER_NAME)
        if (mHuaweiInterstitialAd!!.isLoaded) {
            mHuaweiInterstitialAd!!.show()
        } else {
            MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_FAILED, ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_NO_FILL.intCode,
                MoPubErrorCode.NETWORK_NO_FILL
            )
            if (mInteractionListener != null) {
                mInteractionListener.onAdFailed(MoPubErrorCode.NETWORK_NO_FILL)
            }
        }
    }

    override fun onInvalidate() {
        if (mHuaweiInterstitialAd != null) {
            mHuaweiInterstitialAd!!.adListener = null
            mHuaweiInterstitialAd = null
        }
    }

    override fun getLifecycleListener(): LifecycleListener? {
        return null
    }

    private fun extrasAreValid(extras: Map<String, String>): Boolean {
        return extras.containsKey(AD_UNIT_ID_KEY)
    }

    override fun getAdNetworkId(): String {
        return if (mAdUnitId == null) "" else mAdUnitId!!
    }

    override fun checkAndInitializeSdk(launcherActivity: Activity,
                                       adData: AdData
    ): Boolean {
        return false
    }

    private inner class InterstitialAdListener : AdListener() {
        override fun onAdClosed() {
            if (mInteractionListener != null) {
                mInteractionListener.onAdDismissed()
            }
        }

        override fun onAdFailed(loadAdError: Int) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                    getMoPubErrorCode(loadAdError)!!.intCode,
                    getMoPubErrorCode(loadAdError))
            MoPubLog.log(adNetworkId, AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Failed to load Huawei " +
                    "interstitial with message: " + getMoPubErrorCode(loadAdError)!!.name + ". Caused by: " +
                    loadAdError)

            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(getMoPubErrorCode(loadAdError)!!)
            }
        }

        override fun onAdLeave() {
            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked()
            }
        }

        override fun onAdLoaded() {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME)
            if (mLoadListener != null) {
                mLoadListener.onAdLoaded()
            }
        }

        override fun onAdOpened() {
            MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME)
            if (mInteractionListener != null) {
                mInteractionListener.onAdShown()
                mInteractionListener.onAdImpression()
            }
        }

        private fun getMoPubErrorCode(error: Int): MoPubErrorCode? {
            return when (error) {
                AdParam.ErrorCode.INNER -> MoPubErrorCode.INTERNAL_ERROR
                AdParam.ErrorCode.INVALID_REQUEST -> MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR
                AdParam.ErrorCode.NETWORK_ERROR -> MoPubErrorCode.NO_CONNECTION
                AdParam.ErrorCode.NO_AD -> MoPubErrorCode.NO_FILL
                else -> MoPubErrorCode.UNSPECIFIED
            }
        }
    }
}