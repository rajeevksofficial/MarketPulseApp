package com.marketpulse.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marketpulse.app.databinding.ItemHomeHeaderBinding

class MarketHomeHeaderAdapter(
    private var indices: List<StockItem> = emptyList(),
    private var news: List<MarketNews> = emptyList(),
    private var sectionTitle: String = "Equities & Stocks",
    private var showOverview: Boolean = true,
    private val onIndexClick: (StockItem) -> Unit,
    private val onNewsClick: (MarketNews) -> Unit
) : RecyclerView.Adapter<MarketHomeHeaderAdapter.HeaderViewHolder>() {

    private val indexAdapter = MarketIndexAdapter(indices, onIndexClick)
    private val newsAdapter = MarketNewsAdapter(news, onNewsClick)

    fun update(
        newIndices: List<StockItem>,
        newNews: List<MarketNews>,
        newTitle: String,
        newShowOverview: Boolean
    ) {
        val changed = indices != newIndices || news != newNews || sectionTitle != newTitle || showOverview != newShowOverview
        if (changed) {
            indices = newIndices
            news = newNews
            sectionTitle = newTitle
            showOverview = newShowOverview
            indexAdapter.updateList(indices)
            newsAdapter.updateList(news)
            notifyItemChanged(0)
        }
    }

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHomeHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    inner class HeaderViewHolder(private val binding: ItemHomeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rvMarketIndices.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = indexAdapter
            }
            binding.rvMarketNews.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = newsAdapter
            }
        }

        fun bind() {
            binding.tvSectionHeader.text = sectionTitle

            if (showOverview) {
                binding.cardMarketOverview.visibility = View.VISIBLE
                binding.rvMarketIndices.visibility = View.VISIBLE
                binding.rvMarketNews.visibility = View.VISIBLE
            } else {
                binding.cardMarketOverview.visibility = View.GONE
                // When actively searching, hide the big overview card to keep search results front-and-center!
            }

            // Calculate overall gainers vs losers
            val allStocks = MarketRepository.getAllStocks()
            val gainers = allStocks.count { it.changePercent > 0 }
            val losers = allStocks.count { it.changePercent < 0 }
            val topGainer = allStocks.filter { !it.isIndex }.maxByOrNull { it.changePercent }

            val topGainerStr = if (topGainer != null) "${topGainer.symbol} (+${String.format(java.util.Locale.US, "%.2f", topGainer.changePercent)}%)" else "NVDA (+4.01%)"
            binding.tvOverviewStats.text = "$gainers Gainers • $losers Losers • Top Gainer: $topGainerStr • NIFTY +1.30%"
        }
    }
}
