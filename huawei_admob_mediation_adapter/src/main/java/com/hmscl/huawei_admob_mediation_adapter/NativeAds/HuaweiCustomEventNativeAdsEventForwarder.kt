package com.hmscl.huawei_admob_mediation_adapter.NativeAds

import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsEventForwarder (
    private val listener: CustomEventNativeListener,
    private val options: NativeAdOptions
    ) : HuaweiCustomEventNativeAdsListener() {
    override fun onAdClosed() {
        super.onAdClosed()
    }

    override fun onAdFailed(p0: Int) {
        super.onAdFailed(p0)
    }

    override fun onAdLeave() {
        super.onAdLeave()
    }

    override fun onAdOpened() {
        super.onAdOpened()
    }

    override fun onAdLoaded() {
        super.onAdLoaded()
    }

    override fun onAdClicked() {
        listener.onAdClicked()
    }

    override fun onAdImpression() {
        super.onAdImpression()
    }
}