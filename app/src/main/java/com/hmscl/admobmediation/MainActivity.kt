package com.hmscl.admobmediation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var currentNativeAd: UnifiedNativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this)
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
        val adBanner = findViewById<AdView>(R.id.adBanner)
        adBanner.loadAd(adRequest)
    }

    private fun loadInterstitialAds() {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.admob_interstitialAdId)
        interstitialAd.loadAd(AdRequest.Builder().build())
        btn_showInterstitial.setOnClickListener {
            interstitialAd.show()
            loadInterstitialAds() //re-initialize the ad, otherwise it will only be shown once
        }
    }

    private fun loadNativeAds(id: String) {
        val builder = AdLoader.Builder(this,id)

        builder.forUnifiedNativeAd {unifiedNativeAd ->
            val adView = layoutInflater.inflate(R.layout.view_native_ad_unified,null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd,adView)
            ad_frame.removeAllViews()
            ad_frame.addView(adView)
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Toast.makeText(this@MainActivity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show()
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
        val rewardedAd = RewardedAd(this, getString(R.string.admob_rewardedAdId))
        rewardedAd.loadAd(AdRequest.Builder().build(),object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                btn_showRewards.isEnabled = true
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(applicationContext,"Rewarded ad failed to load Error code- $errorCode",Toast.LENGTH_SHORT).show()
                btn_showRewards.isEnabled = false
            }
        })

        btn_showRewards.setOnClickListener {
            if (rewardedAd.isLoaded) {
                val rewardedAdCallback = object : RewardedAdCallback() {
                    override fun onUserEarnedReward(reward: RewardItem) {
                        Toast.makeText(applicationContext,"You are rewarded with ${reward.amount} coins!",Toast.LENGTH_SHORT).show()
                        btn_showRewards.isEnabled = false
                    }
                }
                rewardedAd.show(this,rewardedAdCallback)
            }
        }
    }
}