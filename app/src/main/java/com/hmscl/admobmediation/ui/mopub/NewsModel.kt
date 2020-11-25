package com.hmscl.admobmediation.ui.mopub

data class NewsListModel(
    var data: List<NewsModel>
)
data class NewsModel(val title: String?, val description: String?, val link: String?, val pubDate: String?)