package com.hmscl.huawei_admob_mediation_adapter.RewardedAds

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationRewardedAd
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration
import com.google.android.gms.ads.rewarded.RewardItem
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener

class HuaweiCustomEventRewardedAdEventForwarder(
        private val adConfiguration: MediationRewardedAdConfiguration,
        private val mediationAdLoadCallBack: MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback>
): HuaweiCustomEventRewardedAdListener(),MediationRewardedAd {
    private lateinit var rewardedAdCallback: MediationRewardedAdCallback
    private lateinit var rewardAd: RewardAd
    private var rewardAdId = "testx9dtjwj8hp"
    fun load() {

    }

    override fun showAd(context: Context?) {
        rewardAd = RewardAd(context, rewardAdId)
        val listener = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(p0: Int) {
                super.onRewardAdFailedToLoad(p0)
            }

            override fun onRewardedLoaded() {
                rewardAd.show(context as Activity?,object : RewardAdStatusListener() {
                    override fun onRewardAdClosed() {
                        super.onRewardAdClosed()
                    }

                    override fun onRewardAdFailedToShow(p0: Int) {
                        super.onRewardAdFailedToShow(p0)
                    }

                    override fun onRewardAdOpened() {
                        super.onRewardAdOpened()
                    }

                    override fun onRewarded(reward: Reward) {
                        Toast.makeText(context,"Your reward: ${reward.amount}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        rewardAd.loadAd(AdParam.Builder().build(), listener)
    }
}