package com.marketpulse.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.marketpulse.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerAdapter: MarketHomeHeaderAdapter
    private lateinit var stockAdapter: StockListAdapter

    private var selectedCategory: String = "All Assets"
    private var isWatchlistFilterActive: Boolean = false

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupCategoryChips()
        setupWatchlistButton()
        setupSyncAndPullToRefresh()
        updateMarketUI()

        // Background sync on launch using free open Yahoo Finance API & RSS
        syncMarketData(isUserInitiated = false)
    }

    override fun onResume() {
        super.onResume()
        updateMarketUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

    private fun setupRecyclerView() {
        headerAdapter = MarketHomeHeaderAdapter(
            indices = MarketRepository.getIndices(),
            news = MarketRepository.getLatestNews(),
            sectionTitle = "Trending Equities & Stocks",
            showOverview = true,
            onIndexClick = { indexItem ->
                openStockDetail(indexItem)
            },
            onNewsClick = { newsItem ->
                if (newsItem.url.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        stockAdapter = StockListAdapter(
            stockList = emptyList(),
            onBookmarkToggle = { stock ->
                MarketRepository.toggleBookmark(stock.symbol)
            },
            onItemClick = { stock ->
                openStockDetail(stock)
            }
        )

        binding.rvStocks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ConcatAdapter(headerAdapter, stockAdapter)
            setHasFixedSize(true)
        }
    }

    private fun openStockDetail(stock: StockItem) {
        val intent = Intent(this, StockDetailActivity::class.java).apply {
            putExtra(StockDetailActivity.EXTRA_SYMBOL, stock.symbol)
        }
        startActivity(intent)
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable { updateMarketUI() }
                searchHandler.postDelayed(searchRunnable!!, 150)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCategoryChips() {
        val chipMap = mapOf(
            binding.chipAll to "All Assets",
            binding.chipGainers to "🚀 Top Gainers",
            binding.chipLosers to "📉 Top Losers",
            binding.chipIndia to "🇮🇳 Indian Equities",
            binding.chipUS to "🇺🇸 US Tech Giants",
            binding.chipIndices to "📊 Market Indices",
            binding.chipCommodities to "🪙 Commodities",
            binding.chipWatchlist to "⭐ Watchlist"
        )

        for ((chipView, category) in chipMap) {
            chipView.setOnClickListener {
                selectedCategory = category
                isWatchlistFilterActive = (category == "⭐ Watchlist")
                updateChipStyles(chipMap, chipView)
                updateWatchlistIconState()
                updateMarketUI()
            }
        }
    }

    private fun updateChipStyles(chipMap: Map<TextView, String>, selectedChip: TextView) {
        val selectedBg = ContextCompat.getDrawable(this, R.drawable.bg_chip_selected)
        val unselectedBg = ContextCompat.getDrawable(this, R.drawable.bg_chip_unselected)
        val selectedColor = ContextCompat.getColor(this, R.color.white)
        val unselectedColor = ContextCompat.getColor(this, R.color.text_secondary)

        for ((chip, _) in chipMap) {
            if (chip == selectedChip) {
                chip.background = selectedBg
                chip.setTextColor(selectedColor)
            } else {
                chip.background = unselectedBg
                chip.setTextColor(unselectedColor)
            }
        }
    }

    private fun setupWatchlistButton() {
        binding.btnWatchlistFilter.setOnClickListener {
            isWatchlistFilterActive = !isWatchlistFilterActive
            updateWatchlistIconState()
            updateMarketUI()
        }
    }

    private fun updateWatchlistIconState() {
        binding.btnWatchlistFilter.setImageResource(
            if (isWatchlistFilterActive) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
        )
    }

    private fun setupSyncAndPullToRefresh() {
        binding.btnRefresh.setOnClickListener {
            syncMarketData(isUserInitiated = true)
        }

        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary, R.color.accent_gold)
        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.surface)
        binding.swipeRefresh.setOnRefreshListener {
            syncMarketData(isUserInitiated = true)
        }
    }

    private fun syncMarketData(isUserInitiated: Boolean) {
        if (isUserInitiated) {
            val rotate = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 600
                repeatCount = 1
            }
            binding.btnRefresh.startAnimation(rotate)
            Toast.makeText(this, "Fetching live market quotes from Yahoo Finance & Economic Times...", Toast.LENGTH_SHORT).show()
        }

        binding.tvSubtitle.text = "Syncing live quotes from Yahoo Finance & News..."

        MarketRepository.refreshMarketData { success ->
            runOnUiThread {
                binding.swipeRefresh.isRefreshing = false
                if (success) {
                    binding.tvSubtitle.text = "● Live Market Data Updated (26 Assets)"
                    if (isUserInitiated) {
                        Toast.makeText(this@MainActivity, "Market data updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.tvSubtitle.text = "Real-Time Share Markets & Stock Intelligence"
                }
                updateMarketUI()
            }
        }
    }

    private fun updateMarketUI() {
        val query = binding.etSearch.text?.toString()?.trim() ?: ""
        val filtered = MarketRepository.filterStocks(
            category = selectedCategory,
            query = query,
            watchlistOnly = isWatchlistFilterActive
        )

        val sectionTitle = when {
            isWatchlistFilterActive -> "Watchlist Assets (${filtered.size})"
            query.isNotEmpty() -> "Search Results for \"$query\" (${filtered.size})"
            selectedCategory != "All Assets" -> "$selectedCategory (${filtered.size})"
            else -> "Equities & Stocks (${filtered.size})"
        }

        val showOverview = query.isEmpty() && !isWatchlistFilterActive && selectedCategory == "All Assets"

        headerAdapter.update(
            newIndices = MarketRepository.getIndices(),
            newNews = MarketRepository.getLatestNews(),
            newTitle = sectionTitle,
            newShowOverview = showOverview
        )

        stockAdapter.updateList(filtered)

        // Empty state handling
        if (filtered.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvStocks.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvStocks.visibility = View.VISIBLE
        }
    }
}
