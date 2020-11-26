# Huawei Ads <> MoPub mediation adapter
This is a project to demostrate how to use MoPub's mediation feature with Huawei Ads Kit.

## Configure a custom event on MoPub:
1. Sign in to your [Huawei Developer Console](https://developer.huawei.com/consumer/en/console) and create an AdUnit
2. Sign in to your [MoPub console](https://app.mopub.com/)
3. Go to one of your orders and create a new line item
4. Select **Network line item** as the type
5. Select **Custom SDK network** as the network
6. Enter the **Custom event class** and **Custom event data** according to the type of your Ad. Refer to the section below.

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
    implementation 'com.hmscl.huawei.ads.mediation_adapter_mopub:1.1'
}
```

## Custom event class
| Ad Type        | Custom event class           |
| ------------- |:-------------:|
| Banner Ad      | com.hmscl.huawei.ads.mediation_adapter_mopub.banner |
| Interstitial Ad      | com.hmscl.huawei.ads.mediation_adapter_mopub.interstitial     |
| Rewarded Video Ad | com.hmscl.huawei.ads.mediation_adapter_mopub.rewarded     |
| Native Ad (Basic) | ccom.hmscl.huawei.ads.mediation_adapter_mopub.native_nasic    |
| Native Ad (Advanced) | com.hmscl.huawei.ads.mediation_adapter_mopub.native_advanced    |

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

## Native Ad Advanced
You need to register your own Ad Renderer using HuaweiAdsAdRenderer class.
```
val huaweiAdsAdRenderer = HuaweiAdsAdRenderer(
            HuaweiAdsViewBinder.Builder(R.layout.view_mopub_nativead_huawei)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .mediaLayoutId(R.id.native_media_layout)
                .iconImageId(R.id.native_icon_image)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build())

mAdAdapter.registerAdRenderer(huaweiAdsAdRenderer)
```

## Device requirement
- A device with Huawei Mobile Services (HMS) installed
 
## Supported ad type:
1. Banner
2. Interstitial
3. Rewarded
4. Native