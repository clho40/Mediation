package com.hmscl.huawei.ads.mediation_adapter_admob.NativeAds

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsMapper(
    private var huaweiNativeAd: NativeAd,
    private val context: Context
): UnifiedNativeAdMapper() {
    private var bundleData: Bundle? = null
    init {
        if (huaweiNativeAd.choicesInfo.content != "" && huaweiNativeAd.choicesInfo.icons.size > 0) {
            val whyThisAd: Button = Button(context)
            whyThisAd.setCompoundDrawables(huaweiNativeAd.choicesInfo.icons[0].drawable,null,null,null)
            whyThisAd.text = huaweiNativeAd.choicesInfo.content
            whyThisAd.setOnClickListener { huaweiNativeAd.gotoWhyThisAdPage(context) }
            adChoicesContent = whyThisAd //huaweiNativeAd.choicesInfo //missing in doc
        }
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