# AdMobMediation-Demo
This is a project to demostrate how to use Google AdMob's mediation feature with Huawei Ads Kit.

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

## If you would like to use your own Google AdMob AdUnits, Mediation Group:
- You need an activated Google AdMob account
- You need to create matching App, AdUnits, and Mediation Group in Google AdMob console
- In your mediation group, create a custom event and use the following as your class name
> com.hmscl.huawei_admob_mediation_adapter.HuaweiCustomEventAdapter
- Also, input your Huawei Ads AdUnit Id as the parameter. If you provide a Huawei Ads AdUnit Id, a test ad will be displayed

![Custom event setting](https://i.ibb.co/SnfH3Cj/Untitled.png)

- Replace the adUnit id in app\src\main\res\values\strings.xml with your Google AdMob adUnit id

## Explaination:
[How to use Huawei Ads with Google AdMob mediation (Banner Ads)](https://clho40.medium.com/how-to-use-huawei-ads-with-google-admob-mediation-banner-ads-5ff1791e750c)