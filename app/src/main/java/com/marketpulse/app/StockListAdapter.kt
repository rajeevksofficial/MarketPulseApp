package com.marketpulse.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.marketpulse.app.databinding.ItemStockCardBinding

class StockListAdapter(
    private var stockList: List<StockItem> = emptyList(),
    private val onBookmarkToggle: (StockItem) -> Unit,
    private val onItemClick: (StockItem) -> Unit
) : RecyclerView.Adapter<StockListAdapter.StockViewHolder>() {

    fun updateList(newList: List<StockItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = stockList.size
            override fun getNewListSize(): Int = newList.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return stockList[oldItemPosition].symbol == newList[newItemPosition].symbol
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = stockList[oldItemPosition]
                val new = newList[newItemPosition]
                return old.price == new.price &&
                        old.changePercent == new.changePercent &&
                        old.isBookmarked == new.isBookmarked &&
                        old.name == new.name
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stockList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(stockList[position])
    }

    override fun getItemCount(): Int = stockList.size

    inner class StockViewHolder(private val binding: ItemStockCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockItem) {
            val context = binding.root.context

            binding.tvStockSymbol.text = item.symbol
            binding.tvStockName.text = item.name
            binding.tvStockExchange.text = item.exchange
            binding.tvStockCategory.text = "${item.category} • ${item.sentiment}"
            binding.tvStockPrice.text = item.formattedPrice

            val isGain = item.isPositive
            val sign = if (isGain) "+" else ""
            binding.tvStockChange.text = "$sign${String.format(java.util.Locale.US, "%.2f", item.changePercent)}%"
            binding.tvStockChange.background = ContextCompat.getDrawable(
                context,
                if (isGain) R.drawable.bg_pill_gain else R.drawable.bg_pill_loss
            )
            binding.tvStockChange.setTextColor(
                if (isGain) Color.parseColor("#10B981") else Color.parseColor("#EF4444")
            )

            // Sparkline preview
            binding.sparklineStock.setData(item.historicalPoints, isGain, enableFill = true)

            // Bookmark icon
            if (item.isBookmarked) {
                binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                binding.btnBookmark.setImageResource(R.drawable.ic_bookmark)
            }

            binding.btnBookmark.setOnClickListener {
                onBookmarkToggle(item)
                if (item.isBookmarked) {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                } else {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark)
                }
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
