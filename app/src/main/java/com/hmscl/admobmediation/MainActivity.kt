package com.hmscl.admobmediation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this)
        loadBannerAds()
        loadInterstitialAds()
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
}