package com.hmscl.mediationdemo.ui.mopub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hmscl.mediationdemo.R
import com.hmscl.mediationdemo.Utils
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsAdRenderer
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsViewBinder
import com.mopub.common.MoPub
import com.mopub.common.MoPubReward
import com.mopub.common.SdkConfiguration
import com.mopub.common.SdkInitializationListener
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import com.mopub.mobileads.MoPubRewardedVideoListener
import com.mopub.mobileads.MoPubRewardedVideos
import com.mopub.nativeads.*
import kotlinx.android.synthetic.main.fragment_mopub.*

class MopubFragment : Fragment() {
    lateinit var mopubListener: MoPubListeners
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mopub, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mopubListener = MoPubListeners(requireContext())
        initMoPub()
    }
    private fun initMoPub() {
        val configBuilder = SdkConfiguration.Builder("5aee2c59fed54dc1b7e8d24ae87a7c66")
            .withLogLevel(MoPubLog.LogLevel.NONE)
            .build()

        val initSdkListener = SdkInitializationListener {
            Utils.showToast(requireContext(), "MoPub initialized")
            showGDPRConsent()
        }

        MoPub.initializeSdk(requireContext(), configBuilder, initSdkListener)
    }

    private fun showGDPRConsent() {
        val mPersonalInfoManager = MoPub.getPersonalInformationManager()

        if (mPersonalInfoManager?.shouldShowConsentDialog()!!) {
            Utils.showToast(requireContext(), "We assume you agreed to our GDPR privacy policy.")
            mPersonalInfoManager.grantConsent()
        }
        else {
            Utils.showToast(requireContext(), "Consent is already granted")
        }
        startShowingAds()
    }

    private fun startShowingAds() {
        loadBannerAd()
        loadInterstitialAd()
        loadRewardedAd()
//        populateNews()
        loadNativeAd()
    }

    private fun loadBannerAd() {
        mopub_banner.bannerAdListener = mopubListener
        mopub_banner.setAdUnitId("5aee2c59fed54dc1b7e8d24ae87a7c66")
        mopub_banner.loadAd()
    }

    private fun loadInterstitialAd() {
        val mInterstitial = MoPubInterstitial(requireActivity(), "8f9211acce62474ea2e5a1858bc9aa81")
        val listener = object : MoPubInterstitial.InterstitialAdListener {
            override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
                Utils.showToast(requireContext(), "Interstitial loaded")
                btn_showInterstitial.isEnabled = true
                btn_showInterstitial.setOnClickListener {
                    if (mInterstitial.isReady) {
                        mInterstitial.show()
                    }
                }
            }

            override fun onInterstitialFailed(
                interstitial: MoPubInterstitial?,
                errorCode: MoPubErrorCode?
            ) {
                Utils.showToast(
                    requireContext(),
                    "Interstitial failed - error code : ${errorCode?.name}"
                )
            }

            override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
                Utils.showToast(requireContext(), "Interstitial shown")
            }

            override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
                Utils.showToast(requireContext(), "Interstitial clicked")
            }

            override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
                Utils.showToast(requireContext(), "Interstitial dismissed")
            }
        }
        mInterstitial.interstitialAdListener = listener
        mInterstitial.load()
    }

    private fun loadRewardedAd() {
        val listener = object : MoPubRewardedVideoListener {
            override fun onRewardedVideoLoadSuccess(adUnitId: String) {
                Utils.showToast(requireContext(), "Rewarded video loaded")
                btn_showRewards.isEnabled = true
                btn_showRewards.setOnClickListener {
                    MoPubRewardedVideos.showRewardedVideo(adUnitId)
                }
            }

            override fun onRewardedVideoLoadFailure(adUnitId: String, errorCode: MoPubErrorCode) {
                Utils.showToast(
                    requireContext(),
                    "Rewarded video failed to load - error code : ${errorCode?.name}"
                )
            }

            override fun onRewardedVideoStarted(adUnitId: String) {
                Utils.showToast(requireContext(), "Rewarded video started")
            }

            override fun onRewardedVideoPlaybackError(adUnitId: String, errorCode: MoPubErrorCode) {
                Utils.showToast(
                    requireContext(),
                    "Rewarded video playback error - error code : ${errorCode?.name}"
                )
            }

            override fun onRewardedVideoClicked(adUnitId: String) {
                Utils.showToast(requireContext(), "Rewarded video clicked")
            }

            override fun onRewardedVideoClosed(adUnitId: String) {
                Utils.showToast(requireContext(), "Rewarded video closed")
            }

            override fun onRewardedVideoCompleted(
                adUnitIds: MutableSet<String>,
                reward: MoPubReward
            ) {
                Utils.showToast(
                    requireContext(),
                    "Rewarded video completed. ${reward.amount} ${reward.label} are rewarded to you!"
                )
            }
        }
        MoPubRewardedVideos.setRewardedVideoListener(listener)
        MoPubRewardedVideos.loadRewardedVideo("733e031d1a6143baa77603f4a863adbc")
    }

    private fun loadNativeAd() {
        val moPubNativeEventListener = object : NativeAd.MoPubNativeEventListener{
            override fun onClick(view: View?) {

            }

            override fun onImpression(view: View?) {

            }

        }

        val moPubNativeNetworkListener = object : MoPubNative.MoPubNativeNetworkListener {
            override fun onNativeLoad(nativeAd: NativeAd?) {
                val adapterHelper = AdapterHelper(requireContext(), 0, 3)
                val v = adapterHelper.getAdView(
                    null,
                    native_ad_frame,
                    nativeAd,
                    ViewBinder.Builder(0).build()
                )
                nativeAd!!.setMoPubNativeEventListener(moPubNativeEventListener)
                native_ad_frame.addView(v)
            }

            override fun onNativeFail(errorCode: NativeErrorCode) {
            }
        }

        val moPubNative = MoPubNative(
            requireContext(),
            "fe9139d51c8b4cdd9dca41e6d21dc032",
            moPubNativeNetworkListener
        )

        val viewBinder = ViewBinder.Builder(R.layout.view_mopub_nativead)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .mainImageId(R.id.native_ad_main_image)
            .iconImageId(R.id.native_ad_icon_image)
            .callToActionId(R.id.native_cta_button)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build()

        val huaweiAdsAdRenderer = HuaweiAdsAdRenderer(
            HuaweiAdsViewBinder.Builder(R.layout.view_mopub_nativead_huawei)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .mediaLayoutId(R.id.native_media_layout)
                .iconImageId(R.id.native_icon_image)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build())

        val adRenderer = MoPubStaticNativeAdRenderer(viewBinder)
        moPubNative.registerAdRenderer(adRenderer)
        moPubNative.registerAdRenderer(huaweiAdsAdRenderer)
        moPubNative.makeRequest()
    }
}