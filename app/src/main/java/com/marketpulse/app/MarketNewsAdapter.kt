package com.marketpulse.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marketpulse.app.databinding.ItemMarketNewsBinding

class MarketNewsAdapter(
    private var newsList: List<MarketNews> = emptyList(),
    private val onItemClick: (MarketNews) -> Unit
) : RecyclerView.Adapter<MarketNewsAdapter.NewsViewHolder>() {

    fun updateList(newList: List<MarketNews>) {
        newsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemMarketNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size

    inner class NewsViewHolder(private val binding: ItemMarketNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: MarketNews) {
            binding.tvNewsTag.text = news.tag
            binding.tvNewsDate.text = news.pubDate
            binding.tvNewsTitle.text = news.title
            binding.tvNewsDescription.text = news.description
            binding.tvNewsSource.text = "● ${news.source}"

            binding.root.setOnClickListener {
                onItemClick(news)
            }
        }
    }
}
