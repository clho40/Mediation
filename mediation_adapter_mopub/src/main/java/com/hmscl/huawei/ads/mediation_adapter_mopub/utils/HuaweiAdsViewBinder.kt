package com.hmscl.huawei.ads.mediation_adapter_mopub.utils

import java.util.*

class HuaweiAdsViewBinder private constructor (builder: Builder) {
    class Builder(val layoutId: Int) {
        var mediaLayoutId = 0
        var titleId = 0
        var textId = 0
        var iconImageId = 0
        var callToActionId = 0
        var privacyInformationIconImageId = 0
        var sponsoredTextId = 0
        var extras: MutableMap<String, Int>
        fun mediaLayoutId(mediaLayoutId: Int): Builder {
            this.mediaLayoutId = mediaLayoutId
            return this
        }

        fun titleId(titleId: Int): Builder {
            this.titleId = titleId
            return this
        }

        fun textId(textId: Int): Builder {
            this.textId = textId
            return this
        }

        fun iconImageId(iconImageId: Int): Builder {
            this.iconImageId = iconImageId
            return this
        }

        fun callToActionId(callToActionId: Int): Builder {
            this.callToActionId = callToActionId
            return this
        }

        fun privacyInformationIconImageId(privacyInformationIconImageId: Int): Builder {
            this.privacyInformationIconImageId = privacyInformationIconImageId
            return this
        }

        fun sponsoredTextId(sponsoredTextId: Int): Builder {
            this.sponsoredTextId = sponsoredTextId
            return this
        }

        fun addExtras(resourceIds: Map<String, Int>?): Builder {
            extras = HashMap(resourceIds)
            return this
        }

        fun addExtra(key: String, resourceId: Int): Builder {
            extras[key] = resourceId
            return this
        }

        fun build() : HuaweiAdsViewBinder {
            return HuaweiAdsViewBinder(this)
        }

        init {
            extras = HashMap()
        }
    }

    var layoutId = 0
    var mediaLayoutId = 0
    var titleId = 0
    var textId = 0
    var callToActionId = 0
    var iconImageId = 0
    var privacyInformationIconImageId = 0
    var sponsoredTextId = 0
    var extras: Map<String, Int>? = null

    init {
        layoutId = builder.layoutId
        mediaLayoutId = builder.mediaLayoutId
        titleId = builder.titleId
        textId = builder.textId
        callToActionId = builder.callToActionId
        iconImageId = builder.iconImageId
        privacyInformationIconImageId = builder.privacyInformationIconImageId
        sponsoredTextId = builder.sponsoredTextId
        extras = builder.extras
    }
}