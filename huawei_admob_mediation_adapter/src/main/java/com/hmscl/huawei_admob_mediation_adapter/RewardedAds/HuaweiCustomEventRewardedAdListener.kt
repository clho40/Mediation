package com.hmscl.huawei_admob_mediation_adapter.RewardedAds

import com.huawei.hms.ads.reward.RewardAdLoadListener

open class HuaweiCustomEventRewardedAdListener: RewardAdLoadListener() {
    override fun onRewardAdFailedToLoad(errorCode: Int) {
        super.onRewardAdFailedToLoad(errorCode)
    }

    override fun onRewardedLoaded() {
        super.onRewardedLoaded()
    }
}