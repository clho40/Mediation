package com.hmscl.mediationdemo.ui.mopub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hmscl.mediationdemo.R
import com.hmscl.mediationdemo.Utils
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsAdRenderer
import com.hmscl.huawei.ads.mediation_adapter_mopub.utils.HuaweiAdsViewBinder
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.SdkInitializationListener
import com.mopub.common.logging.MoPubLog
import com.mopub.nativeads.*
import kotlinx.android.synthetic.main.fragment_mo_pub_recycler_view.*

class MoPubRecyclerViewFragment : Fragment() {
    lateinit var mopubListener: MoPubListeners
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mo_pub_recycler_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mopubListener = MoPubListeners(requireContext())
        initMoPub()
    }
    private fun initMoPub() {
        val configBuilder = SdkConfiguration.Builder("5aee2c59fed54dc1b7e8d24ae87a7c66")
            .withLogLevel(MoPubLog.LogLevel.NONE)
            .build()

        val initSdkListener = SdkInitializationListener {
            Utils.showToast(requireContext(), "MoPub initialized")
            showGDPRConsent()
        }

        MoPub.initializeSdk(requireContext(), configBuilder, initSdkListener)
    }
    private fun showGDPRConsent() {
        val mPersonalInfoManager = MoPub.getPersonalInformationManager()

        if (mPersonalInfoManager?.shouldShowConsentDialog()!!) {
            Utils.showToast(requireContext(), "We assume you agreed to our GDPR privacy policy.")
            mPersonalInfoManager.grantConsent()
        }
        else {
            Utils.showToast(requireContext(), "Consent is already granted")
        }
        startShowingAds()
    }

    private fun startShowingAds() {
        populateNews()
    }

    private fun getNewsData() : List<NewsModel> {
        //parse sample json data
        var sample_json_data = requireContext().assets.open("news.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(sample_json_data, NewsListModel::class.java).data
    }

    private fun populateNews() {
        val newsCard_viewAdapter = NewsAdapter(getNewsData())
        val adPositioning = MoPubNativeAdPositioning.serverPositioning()
        val mAdAdapter = MoPubRecyclerAdapter(requireActivity(),newsCard_viewAdapter,adPositioning)
        val viewBinder = ViewBinder.Builder(R.layout.view_mopub_nativead)
            .mainImageId(R.id.native_ad_main_image)
            .iconImageId(R.id.native_ad_icon_image)
            .callToActionId(R.id.native_cta_button)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .sponsoredTextId(R.id.native_sponsored_text_view)
            .build()
        val huaweiAdsAdRenderer = HuaweiAdsAdRenderer(
            HuaweiAdsViewBinder.Builder(R.layout.view_mopub_nativead_huawei)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .mediaLayoutId(R.id.native_media_layout)
                .iconImageId(R.id.native_icon_image)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build())
        val adRenderer = MoPubStaticNativeAdRenderer(viewBinder)
        mAdAdapter.registerAdRenderer(adRenderer)
        mAdAdapter.registerAdRenderer(huaweiAdsAdRenderer)
        rv_news.adapter = mAdAdapter
        mAdAdapter.loadAds("d1a43fab7f1b4be2b8f64bc12ed2175f")
        newsCard_viewAdapter.notifyDataSetChanged()
        rv_news.layoutManager = LinearLayoutManager(requireContext())
        rv_news.setHasFixedSize(true)
//        mAdAdapter.setAdLoadedListener(object : MoPubNativeAdLoadedListener{
//            override fun onAdLoaded(position: Int) {
//                Utils.showToast(requireContext(),"Native Ad loaded at position $position")
//            }
//
//            override fun onAdRemoved(position: Int) {
//                Utils.showToast(requireContext(),"Native Ad removed at position $position")
//            }
//        })

    }
}