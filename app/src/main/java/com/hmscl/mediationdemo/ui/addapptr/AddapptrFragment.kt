package com.hmscl.mediationdemo.ui.addapptr

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.hmscl.mediationdemo.R
import com.hmscl.mediationdemo.Utils
import com.intentsoftware.addapptr.AATKit
import com.intentsoftware.addapptr.AATKitConfiguration
import com.intentsoftware.addapptr.BannerPlacementLayout
import com.intentsoftware.addapptr.PlacementSize
import com.intentsoftware.addapptr.ad.VASTAdData
import kotlinx.android.synthetic.main.fragment_addapptr.*


class AddapptrFragment : Fragment(), AATKit.Delegate {
    private lateinit var configuration: AATKitConfiguration
    private var stickyBannerId = -1
    private var multisizeBannerId = -1
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
        configuration.setTestModeAccountId(2426)
        AATKit.init(configuration)
    }

    private fun loadAds() {
        loadStickyBanner()
        loadMultisizeBanner()
    }

    private fun loadStickyBanner() {
        stickyBannerId = AATKit.createPlacement("TestBanner", PlacementSize.Banner320x53)
        val mainLayout = aat_stickybanner as FrameLayout
        val placementView = AATKit.getPlacementView(stickyBannerId)
        mainLayout.addView(placementView)
        AATKit.startPlacementAutoReload(stickyBannerId)
    }

    private fun loadMultisizeBanner() {
        multisizeBannerId = AATKit.createPlacement("TestMultisizeBanner", PlacementSize.MultiSizeBanner)
        val mainLayout = aat_multisizebanner as FrameLayout
        AATKit.startPlacementAutoReload(multisizeBannerId);
    }

    private fun unloadBanners() {
        AATKit.stopPlacementAutoReload(stickyBannerId)
        val stickyBannerView = AATKit.getPlacementView(stickyBannerId)
        if (stickyBannerView.parent != null) {
            val parent = stickyBannerView.parent as ViewGroup
            parent.removeView(stickyBannerView)
        }

        AATKit.stopPlacementAutoReload(multisizeBannerId)
        val multisizeBannerView = AATKit.getPlacementView(multisizeBannerId)
        if (multisizeBannerView.parent != null) {
            val parent = multisizeBannerView.parent as ViewGroup
            parent.removeView(multisizeBannerView)
        }
    }

    override fun onResume() {
        super.onResume()
        AATKit.onActivityResume(activity)
        loadAds()
    }

    override fun onPause() {
        super.onPause()
        unloadBanners()
        AATKit.onActivityPause(activity)
    }

    override fun aatkitHaveAd(placementId: Int) {
        Utils.showToast(requireContext(), "Have ad")
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
        Utils.showToast(requireContext(), "User earned")
    }

    override fun aatkitObtainedAdRules(fromTheServer: Boolean) {
        Utils.showToast(requireContext(), "Obtained Ad Rules - $fromTheServer")
    }

    override fun aatkitUnknownBundleId() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }
}