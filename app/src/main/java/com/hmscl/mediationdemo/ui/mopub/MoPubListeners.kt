package com.hmscl.mediationdemo.ui.mopub

import android.content.Context
import com.hmscl.mediationdemo.Utils
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import com.mopub.mobileads.MoPubView

class MoPubListeners(private val context: Context) : MoPubView.BannerAdListener, MoPubInterstitial.InterstitialAdListener {
    override fun onBannerLoaded(banner: MoPubView) {
        Utils.showToast(context, "Banner loaded")
    }

    override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
        Utils.showToast(context, "Banner failed to load - ${errorCode!!.name}")
    }

    override fun onBannerClicked(banner: MoPubView?) {
        Utils.showToast(context, "Banner clicked")
    }

    override fun onBannerExpanded(banner: MoPubView?) {
        Utils.showToast(context, "Banner expanded")
    }

    override fun onBannerCollapsed(banner: MoPubView?) {
        Utils.showToast(context, "Banner collapsed")
    }

    override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
        TODO("Not yet implemented")
    }

    override fun onInterstitialFailed(
        interstitial: MoPubInterstitial?,
        errorCode: MoPubErrorCode?
    ) {
        TODO("Not yet implemented")
    }

    override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
        TODO("Not yet implemented")
    }

    override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
        TODO("Not yet implemented")
    }

    override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
        TODO("Not yet implemented")
    }

    companion object {
        lateinit var interstitialAd: MoPubInterstitial
    }
}