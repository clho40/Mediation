package com.hmscl.huawei.ads.mediation_adapter_admob.NativeAds

import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.android.gms.ads.formats.NativeAd
import com.huawei.hms.ads.Image

class HuaweiCustomEventNativeAdsImageMapper(private val icon: Image): NativeAd.Image() {
    override fun getDrawable(): Drawable {
        return icon.drawable
    }

    override fun getUri(): Uri {
        return icon.uri
    }

    override fun getScale(): Double {
        return icon.scale
    }

    override fun getWidth(): Int {
        return icon.width
    }

    override fun getHeight(): Int {
        return icon.height
    }
}