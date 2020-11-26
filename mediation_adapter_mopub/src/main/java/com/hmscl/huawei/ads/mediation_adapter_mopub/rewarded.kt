package com.hmscl.huawei.ads.mediation_adapter_mopub

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsAdapterConfiguration
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsCustomEventDataKeys
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.TagForChild
import com.huawei.hms.ads.UnderAge
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener
import com.mopub.common.LifecycleListener
import com.mopub.common.MoPubReward
import com.mopub.common.Preconditions
import com.mopub.common.logging.MoPubLog
import com.mopub.common.logging.MoPubLog.AdapterLogEvent
import com.mopub.mobileads.AdData
import com.mopub.mobileads.BaseAd
import com.mopub.mobileads.MoPubErrorCode
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class rewarded : BaseAd() {
    private val KEY_EXTRA_APPLICATION_ID = HuaweiAdsCustomEventDataKeys.KEY_EXTRA_APPLICATION_ID
    private val KEY_EXTRA_AD_UNIT_ID = HuaweiAdsCustomEventDataKeys.AD_UNIT_ID_KEY
    private val KEY_CONTENT_URL = HuaweiAdsCustomEventDataKeys.CONTENT_URL_KEY
    private val TAG_FOR_CHILD_DIRECTED_KEY = HuaweiAdsCustomEventDataKeys.TAG_FOR_CHILD_DIRECTED_KEY
    private val TAG_FOR_UNDER_AGE_OF_CONSENT_KEY =
        HuaweiAdsCustomEventDataKeys.TAG_FOR_UNDER_AGE_OF_CONSENT_KEY
    private val ADAPTER_NAME: String = rewarded::class.java.getSimpleName()
    private var sIsInitialized = AtomicBoolean(false)
    private var mAdUnitId: String? = null
    private var mRewardedAd: RewardAd? = null
    private var mIsLoaded = false
    private var mWeakActivity: WeakReference<Activity?>? = null
    private var mHuaweiAdsAdapterConfiguration = HuaweiAdsAdapterConfiguration()

    override fun getLifecycleListener(): LifecycleListener? {
        return null
    }

    override fun getAdNetworkId(): String {
        return if (mAdUnitId == null) "" else mAdUnitId!!
    }

    override fun onInvalidate() {
        if (mRewardedAd != null) {
            mRewardedAd = null
        }
    }

    @Throws(Exception::class)
    override fun checkAndInitializeSdk(launcherActivity: Activity,
                                       adData: AdData
    ): Boolean {
        Preconditions.checkNotNull(launcherActivity)
        Preconditions.checkNotNull(adData)
        if (!sIsInitialized!!.getAndSet(true)) {
            val extras = adData.extras
            if (TextUtils.isEmpty(extras[KEY_EXTRA_APPLICATION_ID])) {
                HwAds.init(launcherActivity)
            } else {
                HwAds.init(launcherActivity, extras[KEY_EXTRA_APPLICATION_ID])
            }
            mAdUnitId = extras[KEY_EXTRA_AD_UNIT_ID]
            if (TextUtils.isEmpty(mAdUnitId)) {
                MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                        MoPubErrorCode.NETWORK_NO_FILL.intCode,
                    MoPubErrorCode.NETWORK_NO_FILL
                )
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL)
                }
                return false
            }
            mHuaweiAdsAdapterConfiguration.setCachedInitializationParameters(launcherActivity,
                    extras)
            return true
        }
        return false
    }

    override fun load(context: Context, adData: AdData) {
        setAutomaticImpressionAndClickTracking(false)
        val extras = adData.extras
        mAdUnitId = extras[KEY_EXTRA_AD_UNIT_ID]!!
        if (TextUtils.isEmpty(mAdUnitId)) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                    MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.intCode,
                MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR
            )
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
            }
            return
        }
        if (context !is Activity) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Context passed to load " +
                    "was not an Activity. This is a bug in MoPub.")
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
            }
            return
        }
        mWeakActivity = WeakReference(context)
        mRewardedAd = RewardAd(context, mAdUnitId)
        val builder = AdParam.Builder()
        builder.setRequestOrigin("MoPub")

        val contentUrl = extras[KEY_CONTENT_URL]

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
        mRewardedAd!!.loadAd(adRequest, mRewardedAdLoadCallback)
        MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_ATTEMPTED, ADAPTER_NAME)
    }

    private fun hasVideoAvailable(): Boolean {
        return mRewardedAd != null && mIsLoaded
    }

    override fun show() {
        MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_ATTEMPTED, ADAPTER_NAME)
        if (hasVideoAvailable() && mWeakActivity != null && mWeakActivity!!.get() != null) {
            mRewardedAd!!.show(mWeakActivity!!.get(), mRewardedAdCallback)
        } else {
            MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_FAILED, ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_NO_FILL.intCode,
                MoPubErrorCode.NETWORK_NO_FILL
            )
            if (mInteractionListener != null) {
                mInteractionListener.onAdFailed(getMoPubErrorCode(AdParam.ErrorCode.NO_AD)!!)
            }
        }
    }

    private val mRewardedAdLoadCallback = object : RewardAdLoadListener() {
        override fun onRewardAdFailedToLoad(loadAdError: Int) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME)
            MoPubLog.log(adNetworkId, AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Failed to load Huawei " +
                    "rewarded video with message: " + loadAdError + ". Caused by: " +
                    loadAdError)
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(getMoPubErrorCode(loadAdError)!!)
            }
        }

        override fun onRewardedLoaded() {
            mIsLoaded = true
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_SUCCESS, ADAPTER_NAME)
            if (mLoadListener != null) {
                mLoadListener.onAdLoaded()
            }
        }
    }

    private val mRewardedAdCallback = object : RewardAdStatusListener() {
        override fun onRewardAdOpened() {
            MoPubLog.log(adNetworkId, AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME)
            if (mInteractionListener != null) {
                mInteractionListener.onAdShown()
                mInteractionListener.onAdImpression()
            }
        }

        override fun onRewardAdClosed() {
            MoPubLog.log(adNetworkId, AdapterLogEvent.DID_DISAPPEAR, ADAPTER_NAME)
            if (mInteractionListener != null) {
                mInteractionListener.onAdDismissed()
            }
        }

        override fun onRewarded(rewardItem: Reward) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.SHOULD_REWARD, ADAPTER_NAME,
                    rewardItem.amount, rewardItem.name )
            if (mInteractionListener != null) {
                mInteractionListener.onAdComplete(MoPubReward.success(rewardItem.name,
                        rewardItem.amount))
            }
        }

        override fun onRewardAdFailedToShow(loadAdError: Int) {
            MoPubLog.log(adNetworkId, AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                    getMoPubErrorCode(loadAdError)!!.intCode,
                    getMoPubErrorCode(loadAdError))
            MoPubLog.log(adNetworkId, AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Failed to load Huawei " +
                    "rewarded video with message: " + getMoPubErrorCode(loadAdError)!!.name + ". Caused by: " +
                    loadAdError)

            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(getMoPubErrorCode(loadAdError)!!)
            }
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