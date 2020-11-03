package com.hmscl.huawei_admob_mediation_adapter.NativeAds

import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsMapper(
    private var huaweiNativeAd: NativeAd
): UnifiedNativeAdMapper() {
    private var bundleData: Bundle? = null
    init {
        adChoicesContent = null //huaweiNativeAd.choicesInfo //missing in doc
        advertiser = huaweiNativeAd.adSource
        body = huaweiNativeAd.description
        callToAction = huaweiNativeAd.callToAction
        extras = huaweiNativeAd.extraBundle //missing in doc
        headline = huaweiNativeAd.title

        if (huaweiNativeAd.icon != null) {
            icon = HuaweiCustomEventNativeAdsImageMapper(huaweiNativeAd.icon)
        }

        if (huaweiNativeAd.images != null) {
            val imagesList = mutableListOf<com.google.android.gms.ads.formats.NativeAd.Image>()
            for (image in huaweiNativeAd.images) {
                imagesList.add(HuaweiCustomEventNativeAdsImageMapper(image))
            }
            images = imagesList
        }

        if (huaweiNativeAd.mediaContent != null) {
            setMediaView(huaweiNativeAd.mediaContent as View) //missing in doc
            setHasVideoContent(huaweiNativeAd.videoOperator.hasVideo())
            mediaContentAspectRatio = huaweiNativeAd.mediaContent.aspectRatio
        }

        overrideClickHandling = false
        overrideImpressionRecording = false
        price = huaweiNativeAd.price //missing in doc
        starRating = huaweiNativeAd.rating //missing in doc
        store = huaweiNativeAd.market //missing in doc
//        trackViews()
//        untrackView()
    }

    override fun recordImpression() {
        huaweiNativeAd.recordImpressionEvent(bundleData)
    }

    override fun handleClick(view: View?) {
        huaweiNativeAd.triggerClick(bundleData)
    }
}