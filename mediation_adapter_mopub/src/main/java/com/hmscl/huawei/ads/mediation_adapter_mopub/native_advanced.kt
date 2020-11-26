package com.hmscl.huawei.ads.mediation_adapter_mopub

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsAdapterConfiguration
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsCustomEventDataKeys
import com.huawei.hms.ads.*
import com.huawei.hms.ads.nativead.NativeAd
import com.huawei.hms.ads.nativead.NativeAdConfiguration
import com.huawei.hms.ads.nativead.NativeAdLoader
import com.mopub.common.Preconditions
import com.mopub.common.logging.MoPubLog
import com.mopub.common.logging.MoPubLog.AdapterLogEvent
import com.mopub.nativeads.BaseNativeAd
import com.mopub.nativeads.CustomEventNative
import com.mopub.nativeads.NativeErrorCode
import com.mopub.nativeads.NativeImageHelper
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class native_advanced : CustomEventNative() {
    private val KEY_EXTRA_AD_UNIT_ID = HuaweiAdsCustomEventDataKeys.AD_UNIT_ID_KEY
    private val KEY_EXTRA_ORIENTATION_PREFERENCE = HuaweiAdsCustomEventDataKeys.KEY_EXTRA_ORIENTATION_PREFERENCE
    private val KEY_EXTRA_AD_CHOICES_PLACEMENT = HuaweiAdsCustomEventDataKeys.KEY_EXTRA_AD_CHOICES_PLACEMENT
    private val KEY_EXPERIMENTAL_EXTRA_SWAP_MARGINS = HuaweiAdsCustomEventDataKeys.KEY_EXPERIMENTAL_EXTRA_SWAP_MARGINS
    private val KEY_CONTENT_URL = HuaweiAdsCustomEventDataKeys.CONTENT_URL_KEY
    private val TAG_FOR_CHILD_DIRECTED_KEY = HuaweiAdsCustomEventDataKeys.TAG_FOR_CHILD_DIRECTED_KEY
    private val TAG_FOR_UNDER_AGE_OF_CONSENT_KEY = HuaweiAdsCustomEventDataKeys.TAG_FOR_UNDER_AGE_OF_CONSENT_KEY
    private val ADAPTER_NAME = native_advanced::class.java.simpleName
    private val sIsInitialized = AtomicBoolean(false)
    private var mAdUnitId: String? = null
    private var mHuaweiAdapterConfiguration = HuaweiAdsAdapterConfiguration()

    override fun loadNativeAd(context: Context,
                              customEventNativeListener: CustomEventNativeListener,
                              localExtras: Map<String?, Any?>,
                              serverExtras: Map<String?, String?>) {
        Preconditions.checkNotNull(context)
        Preconditions.checkNotNull(customEventNativeListener)
        Preconditions.checkNotNull(localExtras)
        if (!sIsInitialized.getAndSet(true)) {
            HwAds.init(context)
        }
        mAdUnitId = serverExtras[KEY_EXTRA_AD_UNIT_ID]
        if (TextUtils.isEmpty(mAdUnitId)) {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL)
            MoPubLog.log(getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                    NativeErrorCode.NETWORK_NO_FILL.intCode,
                NativeErrorCode.NETWORK_NO_FILL
            )
            return
        }
        val nativeAd = HuaweiNativeAd(customEventNativeListener)
        nativeAd.loadAd(context, mAdUnitId, localExtras)
        mHuaweiAdapterConfiguration.setCachedInitializationParameters(context, serverExtras)
    }

    inner class HuaweiNativeAd(private var mCustomEventNativeListener: CustomEventNativeListener?) :
        BaseNativeAd() {
        var title: String? = null
        var text: String? = null
        var mainImageUrl: String? = null
        var iconImageUrl: String? = null
        var callToAction: String? = null
        var starRating: Double? = null
        var advertiser: String? = null
        var store: String? = null
        var price: String? = null
        var mediaView: String? = null
        var shouldSwapMargins = false
        var huaweiNativeAd: NativeAd? = null

        fun loadAd(context: Context, adUnitId: String?,localExtras: Map<String?, Any?>) {
            val builder = NativeAdLoader.Builder(context, adUnitId)
            if (localExtras.containsKey(KEY_EXPERIMENTAL_EXTRA_SWAP_MARGINS)) {
                val swapMarginExtra = localExtras[KEY_EXPERIMENTAL_EXTRA_SWAP_MARGINS]
                if (swapMarginExtra is Boolean) {
                    shouldSwapMargins = swapMarginExtra
                }
            }
            val optionsBuilder = NativeAdConfiguration.Builder()
            optionsBuilder.setRequestMultiImages(false)

            if (localExtras.containsKey(KEY_EXTRA_ORIENTATION_PREFERENCE)
                && isValidOrientationExtra(localExtras[KEY_EXTRA_ORIENTATION_PREFERENCE])
            ) {
                optionsBuilder.setMediaDirection(localExtras[KEY_EXTRA_ORIENTATION_PREFERENCE] as Int)
            }

            if (localExtras.containsKey(KEY_EXTRA_AD_CHOICES_PLACEMENT)
                && isValidAdChoicesPlacementExtra(
                    localExtras[KEY_EXTRA_AD_CHOICES_PLACEMENT]
                )
            ) {
                optionsBuilder.setChoicesPosition(localExtras[KEY_EXTRA_AD_CHOICES_PLACEMENT] as Int)
            }
            val adOptions = optionsBuilder.build()
            val adLoader = builder.setNativeAdLoadedListener {
                it
                if (!isValidHuaweiNativeAd(it)) {
                    MoPubLog.log(
                        getAdNetworkId(), AdapterLogEvent.CUSTOM, ADAPTER_NAME,
                        "The Huawei native unified ad is missing one or " +
                                "more required assets, failing request."
                    )
                    mCustomEventNativeListener!!.onNativeAdFailed(
                        NativeErrorCode.NETWORK_NO_FILL
                    )
                    MoPubLog.log(
                        getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                        NativeErrorCode.NETWORK_NO_FILL.intCode,
                        NativeErrorCode.NETWORK_NO_FILL
                    )
                } else {
                    huaweiNativeAd = it
                    val images: List<Image> = it.images
                    val imageUrls: MutableList<String> = ArrayList()
                    val mainImage: Image = images[0]
                    imageUrls.add(mainImage.uri.toString().replace("http://", "https://"))
                    if (it.icon != null) {
                        val iconImage: Image = it.icon
                        // Assuming that the URI provided is an URL.
                        imageUrls.add(iconImage.uri.toString().replace("http://","https://"))
                    }
                    preCacheImages(context, imageUrls)
                }
            }.setAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    notifyAdClicked()
                    MoPubLog.log(getAdNetworkId(), AdapterLogEvent.CLICKED, ADAPTER_NAME)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    notifyAdImpressed()
                    MoPubLog.log(getAdNetworkId(), AdapterLogEvent.SHOW_SUCCESS, ADAPTER_NAME)
                }

                override fun onAdFailed(loadAdError: Int) {
                    MoPubLog.log(
                        getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                        NativeErrorCode.NETWORK_NO_FILL.intCode,
                        NativeErrorCode.NETWORK_NO_FILL
                    )
                    MoPubLog.log(
                        getAdNetworkId(), AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Failed to " +
                                "load Huawei native ad with message: " + loadAdError +
                                ". Caused by: " + loadAdError
                    )
                    when (loadAdError) {
                        AdParam.ErrorCode.INNER -> mCustomEventNativeListener!!.onNativeAdFailed(
                            NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR
                        )
                        AdParam.ErrorCode.INVALID_REQUEST -> mCustomEventNativeListener!!.onNativeAdFailed(
                            NativeErrorCode.NETWORK_INVALID_REQUEST
                        )
                        AdParam.ErrorCode.NETWORK_ERROR -> mCustomEventNativeListener!!.onNativeAdFailed(
                            NativeErrorCode.CONNECTION_ERROR
                        )
                        AdParam.ErrorCode.NO_AD -> mCustomEventNativeListener!!.onNativeAdFailed(
                            NativeErrorCode.NETWORK_NO_FILL
                        )
                        else -> mCustomEventNativeListener!!.onNativeAdFailed(
                            NativeErrorCode.UNSPECIFIED
                        )
                    }
                }
            }).setNativeAdOptions(adOptions).build()

            val requestBuilder = AdParam.Builder()
            requestBuilder.setRequestOrigin("MoPub")

            // Publishers may append a content URL by passing it to the MoPubNative.setLocalExtras() call.
            val contentUrl = localExtras[KEY_CONTENT_URL] as String?
            if (!TextUtils.isEmpty(contentUrl)) {
                requestBuilder.setTargetingContentUrl(contentUrl)
            }
            val requestConfigurationBuilder = HwAds.getRequestOptions().toBuilder()

            val childDirected = localExtras[TAG_FOR_CHILD_DIRECTED_KEY]
            if (childDirected != null) {
                if (childDirected.toString().toBoolean()) {
                    requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_TRUE)
                } else {
                    requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_FALSE)
                }
            } else {
                requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_UNSPECIFIED)
            }

            // Publishers may want to mark their requests to receive treatment for users in the
            // European Economic Area (EEA) under the age of consent.
            val underAgeOfConsent = localExtras[TAG_FOR_UNDER_AGE_OF_CONSENT_KEY]
            if (underAgeOfConsent != null) {
                if (underAgeOfConsent.toString().toBoolean()) {
                    requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_TRUE)
                } else {
                    requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_FALSE)
                }
            } else {
                requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_UNSPECIFIED)
            }
            val requestConfiguration = requestConfigurationBuilder.build()
            HwAds.setRequestOptions(requestConfiguration)

            val adRequest = requestBuilder.build()
            adLoader.loadAd(adRequest)
            MoPubLog.log(getAdNetworkId(), AdapterLogEvent.LOAD_ATTEMPTED, ADAPTER_NAME)
        }

        private fun isValidOrientationExtra(extra: Any?): Boolean {
            if (extra !is Int) {
                return false
            }
            return extra == NativeAdConfiguration.Direction.ANY || extra == NativeAdConfiguration.Direction.LANDSCAPE || extra == NativeAdConfiguration.Direction.PORTRAIT
        }

        private fun isValidAdChoicesPlacementExtra(extra: Any?): Boolean {
            if (extra !is Int) {
                return false
            }
            return extra == NativeAdConfiguration.ChoicesPosition.TOP_LEFT || extra == NativeAdConfiguration.ChoicesPosition.TOP_RIGHT || extra == NativeAdConfiguration.ChoicesPosition.BOTTOM_LEFT || extra == NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT
        }

        private fun isValidHuaweiNativeAd(huaweiNativeAd: NativeAd): Boolean {
            return huaweiNativeAd.title != null && huaweiNativeAd.images != null && huaweiNativeAd.images.size > 0 && huaweiNativeAd.images[0] != null && huaweiNativeAd.callToAction != null
//            return huaweiNativeAd.title != null && huaweiNativeAd.description != null && huaweiNativeAd.images != null && huaweiNativeAd.images.size > 0 && huaweiNativeAd.images[0] != null && huaweiNativeAd.icon != null && huaweiNativeAd.callToAction != null
        }

        override fun prepare(view: View) {
            // Adding click and impression trackers is handled by the HuaweiNativeRenderer,
            // do nothing here.
        }

        override fun clear(view: View) {
            mCustomEventNativeListener = null
        }

        override fun destroy() {
            huaweiNativeAd?.destroy()
        }

        private fun preCacheImages(context: Context, imageUrls: List<String>) {
            NativeImageHelper.preCacheImages(context, imageUrls,
                object : NativeImageHelper.ImageListener {
                    override fun onImagesCached() {
                        if (huaweiNativeAd != null) {
                            prepareHuaweiNativeAd(huaweiNativeAd!!)
                            mCustomEventNativeListener!!.onNativeAdLoaded(this@HuaweiNativeAd)
                            MoPubLog.log(
                                getAdNetworkId(),
                                AdapterLogEvent.LOAD_SUCCESS,
                                ADAPTER_NAME
                            )
                        }
                    }

                    override fun onImagesFailedToCache(errorCode: NativeErrorCode) {
                        mCustomEventNativeListener!!.onNativeAdFailed(errorCode)
                        MoPubLog.log(
                            getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, ADAPTER_NAME,
                            errorCode.intCode,
                            errorCode
                        )
                    }
                })
        }

        private fun prepareHuaweiNativeAd(mHuaweiNativeAd: NativeAd) {
            val images: List<Image> = mHuaweiNativeAd.images
            mainImageUrl = images[0].uri.toString().replace("http://","https://")
            if (mHuaweiNativeAd.icon != null) {
                val icon = mHuaweiNativeAd.icon
                iconImageUrl = icon.uri.toString().replace("http://","https://")
            }
            callToAction = mHuaweiNativeAd.callToAction
            title = mHuaweiNativeAd.title
            text = mHuaweiNativeAd.description
            if (mHuaweiNativeAd.rating != null) {
                starRating = mHuaweiNativeAd.rating
            }
            if (mHuaweiNativeAd.market != null) {
                store = mHuaweiNativeAd.market
            }
            if (mHuaweiNativeAd.price != null) {
                price = mHuaweiNativeAd.price
            }
        }
    }

    private fun getAdNetworkId(): String? {
        return mAdUnitId
    }
}