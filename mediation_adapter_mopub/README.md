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
    implementation 'com.hmscl.huawei.ads:mediation_adapter_mopub:1.2'
}
```

## Custom event class
| Ad Type        | Custom event class           |
| ------------- |:-------------:|
| Banner Ad      | com.hmscl.huawei.ads.mediation_adapter_mopub.banner |
| Interstitial Ad      | com.hmscl.huawei.ads.mediation_adapter_mopub.interstitial     |
| Rewarded Video Ad | com.hmscl.huawei.ads.mediation_adapter_mopub.rewarded     |
| Native Ad (Basic) | ccom.hmscl.huawei.ads.mediation_adapter_mopub.native_basic    |
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

# 1. Cross Platforms

## 1.1 Flutter

Banner,Interstitial and rewarded ads are supported by Flutter. Currently there is no Mopub SDK to render native ads in flutter applications. Click [here](https://developer.huawei.com/consumer/en/doc/development/HMS-Plugin-Guides/native-ads-0000001050198817)  to Integrate Huawei Native ads in flutter applications.


 - After the [Custom event configuration](https://github.com/clho40/Mediation/blob/main/mediation_adapter_mopub/README.md#configure-a-custom-event-on-mopub) on Mopub and [SDK integration](https://github.com/clho40/Mediation/tree/main/mediation_adapter_mopub#integrate-the-sdk) to Android side of the flutter project  are done,  follow [this](https://pub.dev/packages/mopub_flutter/install) link to integrate mopub_flutter SDK to your project.



- To avoid "java.lang.RuntimeException: Unable to get provider com.google.android.gms.ads.MobileAdsInitProvider"  error ,  an Admob ID needs to be added  to the  application.If  both Admob and Mopub are not used in the  project , add a sample Admob id to solve this exception.



```
<meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713"/>
            
            //add this meta-data tag to the AndroidManifest.xml file (Open android side of the flutter project to edit this file.)
```


- Check [this](https://pub.dev/packages/mopub_flutter/example) link to see example dart code for mopub_flutter SDK. After 'ad_unit_id' is changed  to IDs created by custom event configuration process, Huawei ads will be succesfully shown in the flutter project using Mopub mediation.

### Code Examples 

- Call Mopub.init() in the initState() of your app
```
try {
      MoPub.init('ad_unit_id', testMode: true).then((_) {
        _loadRewardedAd();
        _loadInterstitialAd();
      });
    } on PlatformException {}
```
- Load a rewarded ad 

```
void _loadRewardedAd() {
    videoAd = MoPubRewardedVideoAd('ad_unit_id',
        (result, args) {
      setState(() {
        rewardedResult = '${result.toString()}____$args';
      });
      print('$result');
      if (result == RewardedVideoAdResult.GRANT_REWARD) {
        print('Grant reward: $args');
      }
    }, reloadOnClosed: true);
  }
```
- Show a rewarded ad
```
   RaisedButton(
                  onPressed: () async {
                    var result = await videoAd.isReady();
                    print('Is Ready $result');
                    if (result) {
                      videoAd.show();
                    }
                  },
                  child: Text('Show Video'),
                )
```

- Load an interstitial ad

```
   void _loadInterstitialAd() {
    interstitialAd = MoPubInterstitialAd(
      'ad_unit_id',
      (result, args) {
        print('Interstitial $result');
      },
      reloadOnClosed: true,
    );    
  }
```
- Show an interstitial ad
 ```
               RaisedButton(
                  onPressed: () async {
                    interstitialAd.show();
                  },
                  child: Text('Show interstitial'),
                )
 ```


- Call a banner ad

```
MoPubBannerAd(
              adUnitId: 'ad_unit_id',
              bannerSize: BannerSize.STANDARD,
              keepAlive: true,
              listener: (result, dynamic) {
                print('$result');
              },
            );
```


## 1.2 React Native

MoPub does not officially support cross platforms. Therefore 3rd party SDKs must be used to integrate MoPub on React Native applications. In this demonstration, 3rd party SDK is used. Click [here](https://www.npmjs.com/package/react-native-mopub-sdk) to get more information about the SDK. 

#### 1. Banner Ads

Banner ads are not supported with this SDK.  To use banner ads in React Native app, please see the HMS Core Ads Kit React Native SDK. Click [here](https://developer.huawei.com/consumer/en/doc/development/HMS-Plugin-Guides/banner-0000001050439147) to get more information about HMS Core React Native SDK.

#### 2. Interstitial Ads

- First import interstitial ads from React Native Mopub SDK:

```   
import { RNMoPubInterstitial} from 'react-native-mopub-sdk';
```

- Next, create a view for the interstitial:

```   
<TouchableOpacity style={{ width: 100, height: 30, backgroundColor: 'red', marginTop: 10 }} onPress={() =>
          RNMoPubInterstitial.loadAd()
        }>
          <Text>
            load Interstitial ad
                  </Text>
        </TouchableOpacity>

```

- Define listeners for the interstitial:

```   
 RNMoPubInterstitial.initializeInterstitialAd(INTERSTITIAL_UNIT_ID);
    RNMoPubInterstitial.addEventListener('onLoaded', () => {
      console.log('Interstitial Loaded')
      RNMoPubInterstitial.show()
    });
    RNMoPubInterstitial.addEventListener('onFailed', message => console.log('Interstitial failed: ' + message));
    RNMoPubInterstitial.addEventListener('onClicked', () => console.log('Interstitial clicked'));
    RNMoPubInterstitial.addEventListener('onShown', () => console.log('Interstitial shown'));
    RNMoPubInterstitial.addEventListener('onDismissed', () => console.log('Interstitial dismissed'));

```

#### 3. Rewarded Ads

Rewarded ads are not supported with this SDK.  To use Rewarded ads in React Native app, please see the HMS Core Ads Kit React Native SDK. Click [here](https://developer.huawei.com/consumer/en/doc/development/HMS-Plugin-Guides/reward-0000001050196920) to get more information about HMS Core React Native SDK.

#### 4. Native Ads

Native ads are not supported with this SDK.  To use Native ads in React Native app, please see the HMS Core Ads Kit React Native SDK. Click [here](https://developer.huawei.com/consumer/en/doc/development/HMS-Plugin-Guides/native-0000001050316236) to get more information about HMS Core React Native SDK.
