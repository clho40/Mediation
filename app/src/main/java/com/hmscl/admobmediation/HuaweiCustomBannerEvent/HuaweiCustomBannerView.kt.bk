package com.hmscl.admobmediation.HuaweiCustomBannerEvent

import android.content.Context
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.banner.BannerView

class HuaweiCustomBannerView(context: Context?) : BannerView(context) {
    var bannerAdId: String? = null

    fun fetchAd() {
        if (bannerAdId == null) {
            //when there's no ad id, use Huawei's test banner id
            bannerAdId = "testw6vs28auh3"
        }
        this.adId = bannerAdId
        this.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        val adParam = AdParam.Builder().build()
        this.loadAd(adParam)
    }
}