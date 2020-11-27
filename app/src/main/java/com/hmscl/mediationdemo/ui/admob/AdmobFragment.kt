package com.hmscl.mediationdemo.ui.admob

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.hmscl.mediationdemo.R
import kotlinx.android.synthetic.main.fragment_admob.*

class AdmobFragment : Fragment() {
    var currentNativeAd: UnifiedNativeAd? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admob, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MobileAds.initialize(requireContext())
        loadBannerAds()
        loadInterstitialAds()
        loadNativeAds(getString(R.string.admob_nativeAdId))
        loadRewardedAds()

        btn_showTestNative.setOnClickListener {
            loadNativeAds(getString(R.string.admob_nativeAdId_test))
        }

        btn_showMediationNative.setOnClickListener {
            loadNativeAds(getString(R.string.admob_nativeAdId))
        }
    }
    private fun loadBannerAds() {
        val adRequest = AdRequest.Builder().build()
        val adBanner = adBanner as AdView
        adBanner.loadAd(adRequest)

        val adRequest2 = PublisherAdRequest.Builder().build()
        publisherAdView.loadAd(adRequest2)
    }

    private fun loadInterstitialAds() {
        val interstitialAd = InterstitialAd(requireContext())
        interstitialAd.adUnitId = getString(R.string.admob_interstitialAdId)
        interstitialAd.loadAd(AdRequest.Builder().build())
        btn_showInterstitial.setOnClickListener {
            interstitialAd.show()
            loadInterstitialAds() //re-initialize the ad, otherwise it will only be shown once
        }
    }

    private fun loadNativeAds(id: String) {
        val builder = AdLoader.Builder(requireContext(),id)

        builder.forUnifiedNativeAd {unifiedNativeAd ->
            val adView = layoutInflater.inflate(R.layout.view_admob_unifiednativead,null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd,adView)
            ad_frame.removeAllViews()
            ad_frame.addView(adView)
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setRequestCustomMuteThisAd(true)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Toast.makeText(requireContext(), "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        currentNativeAd?.destroy()
        currentNativeAd = nativeAd
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)
        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
//            (adView.callToActionView as Button).setOnClickListener {
//                Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
//            }
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    private fun loadRewardedAds() {
        val rewardedAd = RewardedAd(requireContext(), getString(R.string.admob_rewardedAdId))
        rewardedAd.loadAd(AdRequest.Builder().build(),object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                btn_showRewards.isEnabled = true
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(requireContext(),"Rewarded ad failed to load Error code- $errorCode",
                    Toast.LENGTH_SHORT).show()
                btn_showRewards.isEnabled = false
            }
        })

        btn_showRewards.setOnClickListener {
            if (rewardedAd.isLoaded) {
                val rewardedAdCallback = object : RewardedAdCallback() {
                    override fun onUserEarnedReward(reward: RewardItem) {
                        Toast.makeText(requireContext(),"You are rewarded with ${reward.amount} coins!",
                            Toast.LENGTH_SHORT).show()
                        btn_showRewards.isEnabled = false
                    }
                }
                rewardedAd.show(activity,rewardedAdCallback)
            }
        }
    }
}