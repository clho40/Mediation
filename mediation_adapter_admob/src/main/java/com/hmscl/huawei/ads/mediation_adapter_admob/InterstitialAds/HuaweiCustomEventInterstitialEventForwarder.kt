package com.hmscl.huawei.ads.mediation_adapter_admob.InterstitialAds

import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.InterstitialAd
import com.huawei.hms.ads.banner.BannerView

class HuaweiCustomEventInterstitialEventForwarder(
        private var listener: CustomEventInterstitialListener,
        private var huaweiInterstitialView: InterstitialAd
) : AdListener() {
    override fun onAdLoaded() {
        listener.onAdLoaded()
    }

    override fun onAdFailed(errorCode: Int) {
        Log.e("error--",errorCode.toString())
        listener.onAdFailedToLoad(AdError(AdParam.ErrorCode.INNER, AdParam.ErrorCode.INNER.toString(),"HuaweiInterstitialAds"))
    }

    override fun onAdClosed() {
        listener.onAdClosed()
    }

    override fun onAdLeave() {
        listener.onAdLeftApplication()
    }

    override fun onAdOpened() {
        listener.onAdOpened()
    }

    override fun onAdClicked() {
        listener.onAdClicked()

    }

}