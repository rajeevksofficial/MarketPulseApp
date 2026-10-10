package com.marketpulse.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marketpulse.app.databinding.ItemMarketIndexBinding

class MarketIndexAdapter(
    private var indexList: List<StockItem> = emptyList(),
    private val onItemClick: (StockItem) -> Unit
) : RecyclerView.Adapter<MarketIndexAdapter.IndexViewHolder>() {

    fun updateList(newList: List<StockItem>) {
        indexList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndexViewHolder {
        val binding = ItemMarketIndexBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IndexViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndexViewHolder, position: Int) {
        holder.bind(indexList[position])
    }

    override fun getItemCount(): Int = indexList.size

    inner class IndexViewHolder(private val binding: ItemMarketIndexBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockItem) {
            binding.tvIndexName.text = item.name
            binding.tvIndexExchange.text = item.exchange
            binding.tvIndexPrice.text = item.formattedPrice

            val isGain = item.isPositive
            binding.ivTrendIcon.setImageResource(if (isGain) R.drawable.ic_trending_up else R.drawable.ic_trending_down)

            val sign = if (isGain) "+" else ""
            binding.tvIndexChange.text = "$sign${String.format(java.util.Locale.US, "%.2f", item.changePercent)}%"
            binding.tvIndexChange.background = ContextCompat.getDrawable(
                binding.root.context,
                if (isGain) R.drawable.bg_pill_gain else R.drawable.bg_pill_loss
            )
            binding.tvIndexChange.setTextColor(
                if (isGain) Color.parseColor("#10B981") else Color.parseColor("#EF4444")
            )

            // Sparkline preview
            binding.sparklineIndex.setData(item.historicalPoints, isGain, enableFill = true)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
