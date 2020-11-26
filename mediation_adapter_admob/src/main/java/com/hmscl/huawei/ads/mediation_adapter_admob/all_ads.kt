package com.hmscl.huawei.ads.mediation_adapter_admob

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.mediation.*
import com.google.android.gms.ads.mediation.customevent.*
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter
import com.hmscl.huawei.ads.mediation_adapter_admob.BannerAds.HuaweiCustomEventBannerEventForwarder
import com.hmscl.huawei.ads.mediation_adapter_admob.InterstitialAds.HuaweiCustomEventInterstitialEventForwarder
import com.hmscl.huawei.ads.mediation_adapter_admob.NativeAds.HuaweiCustomEventNativeAdsEventForwarder
import com.hmscl.huawei.ads.mediation_adapter_admob.NativeAds.HuaweiCustomEventNativeAdsLoadedEventForwarder
import com.hmscl.huawei.ads.mediation_adapter_admob.RewardedAds.HuaweiCustomEventRewardedAdEventForwarder
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.InterstitialAd
import com.huawei.hms.ads.VideoConfiguration
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.nativead.NativeAdConfiguration
import com.huawei.hms.ads.nativead.NativeAdLoader

class all_ads : Adapter(),
        CustomEventBanner, CustomEventInterstitial, CustomEventNative{
    private val TAG = all_ads::class.java.simpleName

    private lateinit var huaweiBannerView: BannerView
    private var huaweiBannerAdId = "testw6vs28auh3"

    private lateinit var huaweiInterstitialView: InterstitialAd
    private var huaweiInterstitialAdId = "testb4znbuh3n2"

    private lateinit var nativeAdLoader: NativeAdLoader
    private var huaweiNativeAdId = "testu7m3hc4gvm"

    private var huaweiRewardedAdId = "testx9dtjwj8hp"

    override fun requestBannerAd(
            context: Context?,
            listener: CustomEventBannerListener,
            serverParameters: String?,
            size: AdSize,
            mediationAdRequest: MediationAdRequest,
            mediationExtras: Bundle?
    ) {
        try {
            huaweiBannerView= BannerView(context)
            val eventForwarder = HuaweiCustomEventBannerEventForwarder(listener, huaweiBannerView)
            huaweiBannerView.adListener = eventForwarder
            if (serverParameters != null) {
                huaweiBannerAdId = serverParameters
            }
            huaweiBannerView.adId = huaweiBannerAdId
            huaweiBannerView.bannerAdSize = BannerAdSize(size.width, size.height)
            huaweiBannerView.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Exception) {
            Log.e(TAG, "Request Banner Ad Failed - ${e.message}")
            huaweiBannerView.adListener.onAdFailed(AdParam.ErrorCode.INNER)
        }
    }

    override fun requestInterstitialAd(
            context: Context?,
            listener: CustomEventInterstitialListener,
            serverParameters: String?,
            mediationAdRequest: MediationAdRequest,
            mediationExtras: Bundle?
    ) {
        try {
            huaweiInterstitialView = InterstitialAd(context)
            huaweiInterstitialView.adListener = HuaweiCustomEventInterstitialEventForwarder(listener, huaweiInterstitialView)

            if (serverParameters != null) {
                huaweiInterstitialAdId = serverParameters
            }
            huaweiInterstitialView.adId = huaweiInterstitialAdId
            huaweiInterstitialView.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Exception) {
            Log.e(TAG, "Request Interstitial Ad Failed - ${e.message}")
            huaweiInterstitialView.adListener.onAdFailed(AdParam.ErrorCode.INNER)
        }

    }

    override fun showInterstitial() {
        if (huaweiInterstitialView.isLoaded) {
            huaweiInterstitialView.show()
        }
    }

    override fun requestNativeAd(
            context: Context,
            listener: CustomEventNativeListener?,
            serverParameter: String?,
            mediationAdRequest: NativeMediationAdRequest,
            customEventExtras: Bundle?
    ) {
        try {
//            val request = HuaweiCustomEventNativeAdsRequest()
            val options = mediationAdRequest.nativeAdOptions

            if (!mediationAdRequest.isUnifiedNativeAdRequested) {
                listener?.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST)
                return
            }

            val videoConfiguration = VideoConfiguration.Builder()
                    .setStartMuted(options.videoOptions!!.startMuted)
                    .setClickToFullScreenRequested(options.videoOptions!!.clickToExpandRequested)
                    .setCustomizeOperateRequested(options.videoOptions!!.customControlsRequested)
                    .build()

            val adConfiguration= NativeAdConfiguration.Builder()
                    .setVideoConfiguration(videoConfiguration)
                    .setMediaAspect(options.mediaAspectRatio)
                    .setChoicesPosition(options.adChoicesPlacement)
                    .build()

            if (serverParameter != null) {
                huaweiNativeAdId = serverParameter
            }
            val loadedEventForwarder = HuaweiCustomEventNativeAdsLoadedEventForwarder(listener!!, options, context)
            val adEventForwarder = HuaweiCustomEventNativeAdsEventForwarder(listener, options)
            val builder = NativeAdLoader.Builder(context, huaweiNativeAdId)
            builder.setNativeAdOptions(adConfiguration)
            builder.setNativeAdLoadedListener { nativeAd ->
                if (!nativeAdLoader.isLoading) {
                    loadedEventForwarder.onNativeAdLoaded(nativeAd)
                }
            }.setAdListener(adEventForwarder)

            nativeAdLoader = builder.build()
            nativeAdLoader.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Exception) {
            Log.e(TAG, "Request Native Ad Failed - ${e.message}")
        }

    }



    private fun configureAdRequest(bannerAdRequest: MediationAdRequest): AdParam {
        val adParam = AdParam.Builder()
        bannerAdRequest.keywords?.forEach { keyword ->
            adParam.addKeyword(keyword)
        }
        //COPPA
        adParam.setTagForChildProtection(bannerAdRequest.taggedForChildDirectedTreatment())
        //not everything is configured!!
        return adParam.build()
    }

    override fun onDestroy() {
//        Not sure if these are needed
//        huaweiBannerView.adListener = AdListener()
//        huaweiInterstitialView.adListener = AdListener()
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun initialize(
            context: Context?,
            initializationCompleteCallback: InitializationCompleteCallback,
            mediationConfiguration: MutableList<MediationConfiguration>?)
    {

    }

    override fun getVersionInfo(): VersionInfo {
        return VersionInfo(1, 1, 1)
    }

    override fun getSDKVersionInfo(): VersionInfo {
        return VersionInfo(1, 1, 1)
    }

    override fun loadRewardedAd(
            mediationRewardedAdConfiguration: MediationRewardedAdConfiguration?,
            mediationAdLoadCallback: MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback>?)
    {
        val adUnit: String? = mediationRewardedAdConfiguration?.serverParameters?.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD)
        val forwarder = HuaweiCustomEventRewardedAdEventForwarder(mediationRewardedAdConfiguration!!, mediationAdLoadCallback!!)
        forwarder.load(adUnit)
    }
}