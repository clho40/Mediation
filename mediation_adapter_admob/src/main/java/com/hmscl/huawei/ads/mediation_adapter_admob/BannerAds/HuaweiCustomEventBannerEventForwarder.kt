package com.hmscl.huawei.ads.mediation_adapter_admob.BannerAds

import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.banner.BannerView

class HuaweiCustomEventBannerEventForwarder(
        private var listener: CustomEventBannerListener,
        private var huaweiBannerView: BannerView
) : AdListener() {
    override fun onAdLoaded() {
        listener.onAdLoaded(huaweiBannerView)
    }

    override fun onAdFailed(errorCode: Int) {
        Log.e("error--",errorCode.toString())
        listener.onAdFailedToLoad(AdError(AdParam.ErrorCode.INNER, AdParam.ErrorCode.INNER.toString(),"HuaweiBannerAds"))
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