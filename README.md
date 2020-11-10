# Huawei Ads <> Google AdMob mediation adapter
This is a project to demostrate how to use Google AdMob's mediation feature with Huawei Ads Kit.

## Steps:
1. Sign in to your [Huawei Developer Console](https://developer.huawei.com/consumer/en/console) and create an AdUnit
2. Sign in to your [Google AdMob console](https://apps.admob.com/v2)
3. Go to **Mediation > Create a new Mediation Group** (or, use one of your existing Mediation groups)
4. Under the **Ad Sources** section, click **Add Custom Event**. Give it a label (eg: Huawei Banner Custom Event) and an eCPM, click **Continue**
5. Enter _**com.hmscl.huawei_admob_mediation_adapter.HuaweiCustomEventAdapter**_ as the Class Name, and your Huawei's AdUnit ID from step 1 as the parameter
6. Add the adapter and its dependencies into your project (refer to the _How to Use_ section)
6. Done

## How to use
In your project-level build.gradle, include Huawei's Maven repository
```
repositories {
        google()
        jcenter() // Also, make sure jcenter() included
        maven { url 'https://developer.huawei.com/repo/' } // Add this line
}
 ...
allprojects {
    repositories {
        google()
        jcenter() // Also, make sure jcenter() included
        maven { url 'https://developer.huawei.com/repo/' } //Add this line
    }
}
```

In your app-level build.gradle, include Huawei Ads dependency (required by the adapter) and the adapter
```
dependencies {
    ...
    implementation 'com.huawei.hms:ads-lite:13.4.33.300'
    implementation 'com.hmscl:huawei_admob_mediation_adapter:1.2'
}
```

## Device requirement
- A device with Huawei Mobile Services (HMS) installed
 
## Steps to run the project:
1. Generate a keystore file
2. Place the keystore file in the app directory
3. Modify the app module build.gradle file - update the signingConfigs to your keystore's setting
4. Build the project or run the app in your device / emulator

## Supported ad type:
1. Banner
2. Interstitial
3. Rewarded
4. Native

## If you would like to use your own Google AdMob AdUnits, Mediation Group:
- You need an activated Google AdMob account
- You need to create matching App, AdUnits, and Mediation Group in Google AdMob console
- In your mediation group, create a custom event and use the following as your class name
> com.hmscl.huawei_admob_mediation_adapter.HuaweiCustomEventAdapter
- Also, input your Huawei Ads AdUnit Id as the parameter. If you do not provide a Huawei Ads AdUnit Id, a test ad will be displayed

![Custom event setting](https://i.ibb.co/SnfH3Cj/Untitled.png)

- Replace the adUnit id in **app\src\main\res\values\strings.xml** with your Google AdMob adUnit id

## Explaination:
[How to use Huawei Ads with Google AdMob mediation (Banner Ads)](https://clho40.medium.com/how-to-use-huawei-ads-with-google-admob-mediation-banner-ads-5ff1791e750c)