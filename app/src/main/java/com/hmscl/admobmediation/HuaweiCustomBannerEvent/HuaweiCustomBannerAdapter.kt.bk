package com.hmscl.admobmediation.HuaweiCustomBannerEvent

import android.content.Context
import android.os.Bundle
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.mediation.MediationAdRequest
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener

class HuaweiCustomBannerAdapter : CustomEventBanner {
    private lateinit var huaweiBannerView: HuaweiCustomBannerView
    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun requestBannerAd(
        context: Context?,
        listener: CustomEventBannerListener,
        serverParameters: String?,
        size: AdSize,
        mediationAdRequest: MediationAdRequest,
        mediationExtras: Bundle?
    ) {
        huaweiBannerView = HuaweiCustomBannerView(context)
        huaweiBannerView.bannerAdId = serverParameters
        huaweiBannerView.adListener = HuaweiCustomBannerEventForwarder(listener,huaweiBannerView)
        huaweiBannerView.fetchAd()
    }
}