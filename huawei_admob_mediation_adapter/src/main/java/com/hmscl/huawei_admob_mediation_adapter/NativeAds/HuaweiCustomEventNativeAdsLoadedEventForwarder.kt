package com.hmscl.huawei_admob_mediation_adapter.NativeAds

import android.content.Context
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsLoadedEventForwarder(
    private val listener: CustomEventNativeListener,
    private val options: NativeAdOptions,
    private val context: Context
) : HuaweiCustomEventNativeAdsLoadedListener() {
    override fun onNativeAdLoaded(nativeAd: NativeAd) {
        val mapper = HuaweiCustomEventNativeAdsMapper(nativeAd,context)
        listener.onAdLoaded(mapper)
    }
}