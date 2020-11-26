package com.hmscl.huawei.ads.mediation_adapter_admob.RewardedAds

import com.huawei.hms.ads.reward.RewardAdLoadListener

open class HuaweiCustomEventRewardedAdListener: RewardAdLoadListener() {
    override fun onRewardAdFailedToLoad(errorCode: Int) {
        super.onRewardAdFailedToLoad(errorCode)
    }

    override fun onRewardedLoaded() {
        super.onRewardedLoaded()
    }
}