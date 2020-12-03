package com.hmscl.mediationdemo.ui.addapptr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.hmscl.mediationdemo.R
import com.hmscl.mediationdemo.Utils
import com.huawei.hms.ads.nativead.NativeView
import com.intentsoftware.addapptr.*
import com.intentsoftware.addapptr.ad.VASTAdData
import com.millennialmedia.internal.utils.Utils.showToast
import kotlinx.android.synthetic.main.fragment_addapptr.*
import kotlinx.android.synthetic.main.fragment_addapptr.ad_call_to_action
import kotlinx.android.synthetic.main.fragment_addapptr.ad_media


class AddapptrFragment : Fragment(), AATKit.Delegate {
    private lateinit var configuration: AATKitConfiguration
    private var stickyBannerId = -1
    private var multisizeBannerId = -1
    private var fullscreenId = -1
    private var rewardedId = -1
    private var nativeId = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_addapptr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configuration = AATKitConfiguration(requireActivity().application)
        configuration.setDelegate(this)
        AATKit.init(configuration)
    }

    override fun onResume() {
        super.onResume()
        AATKit.onActivityResume(activity)
        loadAds()
    }

    override fun onPause() {
        super.onPause()
        AATKit.onActivityPause(activity)
    }

    private fun loadAds() {
        loadStickyBanner()
        loadMultisizeBanner()
        loadFullscreenAds()
        loadRewardedAds()
        loadNativeAds()
    }

    private fun loadStickyBanner() {
        stickyBannerId = AATKit.createPlacement("TestBanner", PlacementSize.Banner320x53)
        val mainLayout = aat_stickybanner as FrameLayout
        val placementView = AATKit.getPlacementView(stickyBannerId)
        mainLayout.removeAllViews()
        mainLayout.addView(placementView)
        AATKit.startPlacementAutoReload(stickyBannerId)
    }

    private fun loadMultisizeBanner() {
        multisizeBannerId = AATKit.createPlacement("TestMultisizeBanner", PlacementSize.MultiSizeBanner)
//        val mainLayout = aat_multisizebanner as FrameLayout
        AATKit.startPlacementAutoReload(multisizeBannerId);
    }

    private fun loadRewardedAds() {
        rewardedId = AATKit.createRewardedVideoPlacement("RewardedVideo")
        AATKit.startPlacementAutoReload(rewardedId)
    }

    private fun loadFullscreenAds() {
        fullscreenId = AATKit.createPlacement("Fullscreen",PlacementSize.Fullscreen)
        AATKit.startPlacementAutoReload(fullscreenId)
    }

    private fun loadNativeAds() {
        nativeId = AATKit.createNativeAdPlacement("Native",true)
        AATKit.reloadPlacement(nativeId)
    }

    private fun showFullscreenAds() {
        AATKit.showPlacement(fullscreenId)
    }

    private fun showRewardedAds() {
        AATKit.showPlacement(rewardedId)
    }

    private fun showNativeAds() {
        val nativeAd = AATKit.getNativeAd(nativeId)
        if (nativeAd != null && !AATKit.isNativeAdExpired(nativeAd) && AATKit.isNativeAdReady(
                nativeAd
            )
        ) {
            AATKit.reportAdSpaceForPlacement(nativeId)
            val network = AATKit.getNativeAdNetwork(nativeAd)

            var nativeBannerView: ViewGroup? = null
            var mainImageView: View? = null
            var iconView: View? = null

            when (network) {
                AdNetwork.HUAWEI -> {
                    nativeBannerView = native_video_view as NativeView
                    nativeBannerView.titleView = ad_title
                    nativeBannerView.mediaView = ad_media
                    nativeBannerView.adSourceView = ad_source
                    nativeBannerView.callToActionView = ad_call_to_action
                }
                else -> {
                    Utils.showToast(requireContext(),"Ads network not supported")
                }
            }
            ad_title.text = AATKit.getNativeAdTitle(nativeAd)
            ad_call_to_action.text = AATKit.getNativeAdCallToAction(nativeAd)
            ad_call_to_action.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            AATKit.attachNativeAdToLayout(nativeAd, nativeBannerView, mainImageView, iconView)
        }
    }

    override fun aatkitHaveAd(placementId: Int) {
        when (placementId) {
            fullscreenId -> {
                btn_showFullscreenAd.setOnClickListener {
                    showFullscreenAds()
                }
            }
            rewardedId -> {
                btn_showRewardedAd.setOnClickListener {
                    showRewardedAds()
                }
            }
            nativeId -> {
                showNativeAds()
            }
        }
    }

    override fun aatkitNoAd(placementId: Int) {
        Utils.showToast(requireContext(), "No ad")
    }

    override fun aatkitPauseForAd(placementId: Int) {
        Utils.showToast(requireContext(), "Pause for ad")
    }

    override fun aatkitResumeAfterAd(placementId: Int) {
        Utils.showToast(requireContext(), "Resume after ad")
    }

    override fun aatkitShowingEmpty(placementId: Int) {
        Utils.showToast(requireContext(), "Showing empty")
    }

    override fun aatkitUserEarnedIncentive(placementId: Int) {
        Utils.showToast(requireContext(), "aatkitUserEarnedIncentive")
    }

    override fun aatkitObtainedAdRules(fromTheServer: Boolean) {
        Utils.showToast(requireContext(), "Obtained Ad Rules - $fromTheServer")
    }

    override fun aatkitUnknownBundleId() {
        Utils.showToast(requireContext(), "aatkitUnknownBundleId")
    }

    override fun aatkitHaveAdForPlacementWithBannerView(
        placementId: Int,
        bannerView: BannerPlacementLayout?
    ) {
        val mainLayout = aat_multisizebanner as FrameLayout
        mainLayout.removeAllViews()
        mainLayout.addView(bannerView)
    }

    override fun aatkitHaveVASTAd(placementId: Int, data: VASTAdData?) {
        Utils.showToast(requireContext(), "aatkitHaveVASTAd")
    }
}