package com.hmscl.admobmediation.ui.mopub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hmscl.admobmediation.R

class NewsAdapter(var newsList: List<NewsModel>) : RecyclerView.Adapter<NewsAdapter.NewsCardViewHolder>() {
    inner class NewsCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.news_title)
        var description: TextView = itemView.findViewById(R.id.news_shortBody)
        var pubDate: TextView = itemView.findViewById(R.id.news_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsCardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.view_news_card, parent, false)
        return NewsCardViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: NewsCardViewHolder, position: Int) {
        val item = newsList[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.pubDate.text = item.pubDate
    }

    override fun getItemCount() = newsList.size
}