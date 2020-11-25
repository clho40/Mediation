# Huawei Ads <> MoPub mediation adapter
This is a project to demostrate how to use MoPub's mediation feature with Huawei Ads Kit.

## Steps:
1. Sign in to your [Huawei Developer Console](https://developer.huawei.com/consumer/en/console) and create an AdUnit
2. Sign in to your [MoPub console](https://app.mopub.com/)
3. Go to one of your orders and create a new line item
4. Select **Network line item** as the type
5. Select **Custom SDK network** as the network
6. Enter the appropriate **Custom event class** and **Custom event data** according to the type of your Ad. Refer to the section below.

## Integrate the SDK:
In your project-level build.gradle, include Huawei's Maven repository
```
repositories {
        google()
        jcenter()
        maven { url 'https://developer.huawei.com/repo/' } // Add this line
}
 ...
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://developer.huawei.com/repo/' } //Add this line
    }
}
```

In your app-level build.gradle, include Huawei Ads dependency (required by the adapter) and the adapter
```
dependencies {
    ...
    implementation 'com.huawei.hms:ads-lite:13.4.33.300'
    implementation 'com.hmscl:mopub-mediation-huawei:1.0'
}
```

## Custom event class
| Ad Type        | Custom event class           |
| ------------- |:-------------:|
| Banner Ad      | com.mopub.mobileads.HuaweiAdsBanner |
| Interstitial Ad      | com.mopub.mobileads.HuaweiAdsInterstitial     |
| Rewarded Video Ad | com.mopub.mobileads.HuaweiAdsRewardedVideo     |

## Custom event data
```
{
    "adUnitID": "222",  <-- Required
    "appid":"111",
    "tagForChildDirectedTreatment": "false", 
    "TAG_FOR_UNDER_AGE_OF_CONSENT_KEY": "false", 
    "contentUrl" : "abc"
}
```
Other values are optional

## Device requirement
- A device with Huawei Mobile Services (HMS) installed
 
## Supported ad type:
1. Banner
2. Interstitial
3. Rewarded
4. Native