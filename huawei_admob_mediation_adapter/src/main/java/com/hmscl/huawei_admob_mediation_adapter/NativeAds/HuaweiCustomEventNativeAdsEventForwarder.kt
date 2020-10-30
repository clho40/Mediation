package com.hmscl.huawei_admob_mediation_adapter.NativeAds

import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsEventForwarder(
    private val listener: CustomEventNativeListener,
    private val options: NativeAdOptions
) : HuaweiCustomEventNativeAdsListener() {
    override fun onNativeAdLoaded(nativeAd: NativeAd) {
        val mapper = HuaweiCustomEventNativeAdsMapper(nativeAd)
        listener.onAdLoaded(mapper)
    }
}