package com.hmscl.huawei.ads.mediation_adapter_mopub.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.hmscl.huawei.ads.mediation_adapter_mopub.native_advanced
import com.huawei.hms.ads.ChoicesView
import com.huawei.hms.ads.nativead.MediaView
import com.huawei.hms.ads.nativead.NativeView
import com.mopub.common.logging.MoPubLog
import com.mopub.common.logging.MoPubLog.AdapterLogEvent
import com.mopub.nativeads.BaseNativeAd
import com.mopub.nativeads.MoPubAdRenderer
import com.mopub.nativeads.NativeImageHelper
import com.mopub.nativeads.NativeRendererHelper
import java.util.*

class HuaweiAdsAdRenderer(
    private val mViewBinder: HuaweiAdsViewBinder
) : MoPubAdRenderer<native_advanced.HuaweiNativeAd> {
    private val ID_WRAPPING_FRAME = 1001
    private val ID_HUAWEI_NATIVE_VIEW = 1002
    private var mViewHolderMap: WeakHashMap<View, HuaweiStaticNativeViewHolder>? = WeakHashMap()
    private val ADAPTER_NAME: String = HuaweiAdsAdRenderer::class.java.simpleName

    override fun createAdView(context: Context, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(mViewBinder!!.layoutId, parent, false)
        val wrappingView = FrameLayout(context)
        wrappingView.id = ID_WRAPPING_FRAME
        wrappingView.addView(view)
        MoPubLog.log(AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Ad view created.")
        return wrappingView
    }

    override fun renderAdView(view: View, nativeAd: native_advanced.HuaweiNativeAd) {
        var viewHolder = mViewHolderMap!![view]
        if (viewHolder == null) {
            viewHolder = HuaweiStaticNativeViewHolder.fromViewBinder(view, mViewBinder!!)
            mViewHolderMap!![view] = viewHolder
        }
        val huaweiNativeAdView = NativeView(view.context)
        updateHuaweiNativeAdView(nativeAd, viewHolder, huaweiNativeAdView)
        insertHuaweiNativeAdView(huaweiNativeAdView, view, nativeAd.shouldSwapMargins)
    }
    private fun insertHuaweiNativeAdView(huaweiNativeAdView: NativeView,
                                         moPubNativeAdView: View,
                                         swapMargins: Boolean) {
        MoPubLog.log(AdapterLogEvent.SHOW_ATTEMPTED, ADAPTER_NAME)
        if (moPubNativeAdView is FrameLayout
                && moPubNativeAdView.getId() == ID_WRAPPING_FRAME) {
            huaweiNativeAdView.id = ID_HUAWEI_NATIVE_VIEW
            val actualView = moPubNativeAdView.getChildAt(0)
            if (swapMargins) {
                val huaweiNativeAdViewParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val actualViewParams = actualView.layoutParams as FrameLayout.LayoutParams
                huaweiNativeAdViewParams.setMargins(actualViewParams.leftMargin,
                        actualViewParams.topMargin,
                        actualViewParams.rightMargin,
                        actualViewParams.bottomMargin)
                huaweiNativeAdView.layoutParams = huaweiNativeAdViewParams
                actualViewParams.setMargins(0, 0, 0, 0)
            } else {
                huaweiNativeAdView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            moPubNativeAdView.removeView(actualView)
            huaweiNativeAdView.addView(actualView)
            moPubNativeAdView.addView(huaweiNativeAdView)
        } else {
            MoPubLog.log(AdapterLogEvent.CUSTOM, ADAPTER_NAME, "Couldn't add Huawei native ad view. Wrapping view not found.")
        }
    }

    private fun updateHuaweiNativeAdView(staticNativeAd: native_advanced.HuaweiNativeAd,
                                         staticNativeViewHolder: HuaweiStaticNativeViewHolder,
                                         huaweiNativeAdView: NativeView) {
        NativeRendererHelper.addTextView(
            staticNativeViewHolder.mTitleView, staticNativeAd.title
        )
        huaweiNativeAdView.titleView = staticNativeViewHolder.mTitleView
        NativeRendererHelper.addTextView(
            staticNativeViewHolder.mTextView, staticNativeAd.text
        )
        huaweiNativeAdView.descriptionView = staticNativeViewHolder.mTextView
        if (staticNativeViewHolder.mMediaView != null) {
            val mediaview: MediaView = MediaView(huaweiNativeAdView.context)
            staticNativeViewHolder.mMediaView!!.removeAllViews()
            staticNativeViewHolder.mMediaView!!.addView(mediaview)
            huaweiNativeAdView.mediaView = mediaview
        }
        NativeRendererHelper.addTextView(
            staticNativeViewHolder.mCallToActionView,
            staticNativeAd.callToAction
        )
        huaweiNativeAdView.callToActionView = staticNativeViewHolder.mCallToActionView
        NativeImageHelper.loadImageView(
            staticNativeAd.iconImageUrl,
            staticNativeViewHolder.mIconImageView
        )
        huaweiNativeAdView.imageView = staticNativeViewHolder.mIconImageView
        if (staticNativeAd.advertiser != null) {
            NativeRendererHelper.addTextView(
                staticNativeViewHolder.mAdvertiserTextView, staticNativeAd.advertiser
            )
            huaweiNativeAdView.adSourceView = staticNativeViewHolder.mAdvertiserTextView
        }
        // Add the AdChoices icon to the container if one is provided by the publisher.
        if (staticNativeViewHolder.mAdChoicesIconContainer != null) {
            val adChoicesView = ChoicesView(huaweiNativeAdView.context)
            staticNativeViewHolder.mAdChoicesIconContainer!!.removeAllViews()
            staticNativeViewHolder.mAdChoicesIconContainer!!.addView(adChoicesView)
            huaweiNativeAdView.choicesView = adChoicesView
        }

        // Set the privacy information icon to null as the Huawei Ads SDK automatically
        // renders the AdChoices icon.
        NativeRendererHelper.addPrivacyInformationIcon(
            staticNativeViewHolder.mPrivacyInformationIconImageView, null, null
        )
        huaweiNativeAdView.setNativeAd(staticNativeAd.huaweiNativeAd)
    }

    override fun supports(nativeAd: BaseNativeAd): Boolean {
        return nativeAd is native_advanced.HuaweiNativeAd
    }

    private class HuaweiStaticNativeViewHolder {
        var mMainView: View? = null
        var mTitleView: TextView? = null
        var mTextView: TextView? = null
        var mCallToActionView: TextView? = null
        var mIconImageView: ImageView? = null
        var mPrivacyInformationIconImageView: ImageView? = null
        var mStarRatingTextView: TextView? = null
        var mAdvertiserTextView: TextView? = null
        var mStoreTextView: TextView? = null
        var mPriceTextView: TextView? = null
        var mAdChoicesIconContainer: FrameLayout? = null
        var mMediaView: HuaweiAdsMediaLayout? = null
        companion object {
            val VIEW_BINDER_KEY_STAR_RATING = "key_star_rating"
            val VIEW_BINDER_KEY_ADVERTISER = "key_advertiser"
            val VIEW_BINDER_KEY_STORE = "key_store"
            val VIEW_BINDER_KEY_PRICE = "key_price"
            val VIEW_BINDER_KEY_AD_CHOICES_ICON_CONTAINER = "ad_choices_container"
            private val EMPTY_VIEW_HOLDER = HuaweiStaticNativeViewHolder()
            fun fromViewBinder(
                view: View,
                viewBinder: HuaweiAdsViewBinder
            ): HuaweiStaticNativeViewHolder {
                val viewHolder = HuaweiStaticNativeViewHolder()
                viewHolder.mMainView = view
                return try {
                    viewHolder.mTitleView = view.findViewById<View>(viewBinder.titleId) as TextView
                    viewHolder.mTextView = view.findViewById<View>(viewBinder.textId) as TextView
                    viewHolder.mCallToActionView = view.findViewById<View>(viewBinder.callToActionId) as TextView
                    viewHolder.mIconImageView = view.findViewById<View>(viewBinder.iconImageId) as ImageView
                    viewHolder.mPrivacyInformationIconImageView = view.findViewById<View>(viewBinder.privacyInformationIconImageId) as ImageView
                    viewHolder.mMediaView = view.findViewById<View>(viewBinder.mediaLayoutId) as HuaweiAdsMediaLayout
                    val extraViews: Map<String, Int>? = viewBinder.extras
                    val starRatingTextViewId = extraViews?.get(VIEW_BINDER_KEY_STAR_RATING)
                    if (starRatingTextViewId != null) {
                        viewHolder.mStarRatingTextView = view.findViewById<View>(starRatingTextViewId) as TextView
                    }
                    val advertiserTextViewId = extraViews?.get(VIEW_BINDER_KEY_ADVERTISER)
                    if (advertiserTextViewId != null) {
                        viewHolder.mAdvertiserTextView = view.findViewById<View>(advertiserTextViewId) as TextView
                    }
                    val storeTextViewId = extraViews?.get(VIEW_BINDER_KEY_STORE)
                    if (storeTextViewId != null) {
                        viewHolder.mStoreTextView = view.findViewById<View>(storeTextViewId) as TextView
                    }
                    val priceTextViewId = extraViews?.get(VIEW_BINDER_KEY_PRICE)
                    if (priceTextViewId != null) {
                        viewHolder.mPriceTextView = view.findViewById<View>(priceTextViewId) as TextView
                    }
                    val adChoicesIconViewId = extraViews?.get(
                        VIEW_BINDER_KEY_AD_CHOICES_ICON_CONTAINER
                    )
                    if (adChoicesIconViewId != null) {
                        viewHolder.mAdChoicesIconContainer = view.findViewById<View>(adChoicesIconViewId) as FrameLayout
                    }
                    viewHolder
                } catch (exception: ClassCastException) {
                    MoPubLog.log(AdapterLogEvent.CUSTOM_WITH_THROWABLE, "Could not cast from id in ViewBinder to " +
                            "expected View type", exception)
                    EMPTY_VIEW_HOLDER
                }
            }
        }
    }
}