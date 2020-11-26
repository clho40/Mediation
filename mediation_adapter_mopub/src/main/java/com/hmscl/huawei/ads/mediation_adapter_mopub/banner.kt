package com.hmscl.huawei.ads.mediation_adapter_mopub

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsCustomEventDataKeys
import com.huawei.hms.ads.*
import com.huawei.hms.ads.banner.BannerView
import com.mopub.common.LifecycleListener
import com.mopub.common.Preconditions
import com.mopub.common.logging.MoPubLog
import com.mopub.common.logging.MoPubLog.AdapterLogEvent
import com.mopub.common.util.Views
import com.mopub.mobileads.AdData
import com.mopub.mobileads.BaseAd
import com.mopub.mobileads.MoPubErrorCode

class banner: BaseAd() {
    val AD_UNIT_ID_KEY = HuaweiAdsCustomEventDataKeys.AD_UNIT_ID_KEY
    val CONTENT_URL_KEY = HuaweiAdsCustomEventDataKeys.CONTENT_URL_KEY
    val TAG_FOR_CHILD_DIRECTED_KEY = HuaweiAdsCustomEventDataKeys.TAG_FOR_CHILD_DIRECTED_KEY
    val TAG_FOR_UNDER_AGE_OF_CONSENT_KEY =
        HuaweiAdsCustomEventDataKeys.TAG_FOR_UNDER_AGE_OF_CONSENT_KEY
    val ADAPTER_NAME = banner::class.java.simpleName
    private lateinit var mHuaweiAdView: BannerView
    private var mAdUnitId: String? = null
    private var adWidth: Int? = null
    private var adHeight: Int? = null

    override fun load(context: Context, adData: AdData) {
        Preconditions.checkNotNull(context)
        Preconditions.checkNotNull(adData)
        adWidth = adData.adWidth
        adHeight = adData.adHeight
        val extras = adData.extras
        mAdUnitId = extras[AD_UNIT_ID_KEY]

        mHuaweiAdView = BannerView(context)
        mHuaweiAdView.adListener = AdViewListener()
        mHuaweiAdView.adId = mAdUnitId

        val adSize: BannerAdSize? =
            if (adWidth == null || adHeight == null || adWidth!! <= 0 || adHeight!! <= 0) null else BannerAdSize(
                    adWidth!!,
                    adHeight!!
            )

        if (adSize != null) {
            mHuaweiAdView.bannerAdSize = adSize
        } else {
            MoPubLog.log(
                    adNetworkId,
                    AdapterLogEvent.LOAD_FAILED,
                    ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_NO_FILL.intCode,
                MoPubErrorCode.NETWORK_NO_FILL
            )

            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL)
            }
            return
        }

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
        mHuaweiAdView.loadAd(adRequest)
        MoPubLog.log(adNetworkId,AdapterLogEvent.LOAD_ATTEMPTED,ADAPTER_NAME)
    }

    override fun getAdView(): View? {
        return mHuaweiAdView
    }

    override fun onInvalidate() {
        Views.removeFromParent(mHuaweiAdView)
        mHuaweiAdView.adListener = null
        mHuaweiAdView.destroy()
    }

    override fun getLifecycleListener(): LifecycleListener? {
        return null
    }

    override fun getAdNetworkId(): String {
        return if (mAdUnitId == null) "" else mAdUnitId!!
    }

    override fun checkAndInitializeSdk(
            launcherActivity: Activity,
            adData: AdData
    ): kotlin.Boolean {
        return false
    }

    private inner class AdViewListener: AdListener() {
        override fun onAdClosed() {
            super.onAdClosed()
        }

        override fun onAdFailed(loadAdError: Int) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                    getMoPubErrorCode(loadAdError)!!.intCode,
                    getMoPubErrorCode(loadAdError))
            MoPubLog.log(adNetworkId, AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Failed to load Huawei " +
                    "banners with message: " + loadAdError + ". Caused by: " +
                    loadAdError)

            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(getMoPubErrorCode(loadAdError)!!)
            }
        }

        override fun onAdLeave() {
            super.onAdLeave()
        }

        override fun onAdOpened() {
            MoPubLog.log(adNetworkId, AdapterLogEvent.CLICKED, ADAPTER_NAME)

            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked()
            }
        }

        override fun onAdLoaded() {
            val receivedWidth: Int = mHuaweiAdView.bannerAdSize.width
            val receivedHeight: Int = mHuaweiAdView.bannerAdSize.height

            if (receivedWidth > adWidth!! || receivedHeight > adHeight!!) {
                MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                        MoPubErrorCode.NETWORK_NO_FILL.intCode, MoPubErrorCode.NETWORK_NO_FILL
                )
                MoPubLog.log(adNetworkId, AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Huawei served an ad but" +
                        " it was invalidated because its size of " + receivedWidth + " x " + receivedHeight +
                        " exceeds the publisher-specified size of " + adWidth + " x " + adHeight)
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadFailed(getMoPubErrorCode(MoPubErrorCode.NETWORK_NO_FILL.intCode)!!)
                }
            } else {
                MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME)
                MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_ATTEMPTED, ADAPTER_NAME)
                MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME)
                if (mLoadListener != null) {
                    mLoadListener.onAdLoaded()
                }
            }
        }

        override fun onAdClicked() {
            MoPubLog.log(adNetworkId, AdapterLogEvent.CLICKED, ADAPTER_NAME)

            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked()
            }
        }

        override fun onAdImpression() {
            super.onAdImpression()
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