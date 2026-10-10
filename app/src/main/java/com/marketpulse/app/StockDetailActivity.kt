package com.marketpulse.app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marketpulse.app.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SYMBOL = "extra_symbol"
    }

    private lateinit var binding: ActivityStockDetailBinding
    private var currentStock: StockItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val symbol = intent.getStringExtra(EXTRA_SYMBOL) ?: "^NSEI"
        currentStock = MarketRepository.getStockBySymbol(symbol)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val stock = currentStock ?: return

        binding.tvDetailTitleSymbol.text = stock.symbol
        binding.tvDetailTitleExchange.text = "${stock.exchange} • Real-Time Quote"

        binding.tvDetailCategory.text = stock.category
        binding.tvDetailSentiment.text = "Sentiment: ${stock.sentiment}"
        binding.tvDetailCompanyName.text = stock.name

        binding.tvDetailPrice.text = stock.formattedPrice

        val isGain = stock.isPositive
        val sign = if (isGain) "+" else ""
        val currencySym = if (stock.currency == "INR") "₹" else "$"
        binding.tvDetailChange.text = "$sign$currencySym${String.format(java.util.Locale.US, "%.2f", stock.change)} ($sign${String.format(java.util.Locale.US, "%.2f", stock.changePercent)}%)"
        binding.tvDetailChange.background = ContextCompat.getDrawable(
            this,
            if (isGain) R.drawable.bg_pill_gain else R.drawable.bg_pill_loss
        )
        binding.tvDetailChange.setTextColor(
            if (isGain) Color.parseColor("#10B981") else Color.parseColor("#EF4444")
        )

        // Interactive Canvas Chart
        binding.chartDetail.setData(stock.historicalPoints, isGain, enableFill = true)
        binding.tvChartLow.text = "Session Low: $currencySym${String.format(java.util.Locale.US, "%,.2f", stock.dayLow)}"
        binding.tvChartHigh.text = "Session High: $currencySym${String.format(java.util.Locale.US, "%,.2f", stock.dayHigh)}"

        // Key Statistics Grid
        binding.tvStatDayRange.text = "$currencySym${String.format(java.util.Locale.US, "%,.2f", stock.dayLow)} - $currencySym${String.format(java.util.Locale.US, "%,.2f", stock.dayHigh)}"
        binding.tvStat52WRange.text = "$currencySym${String.format(java.util.Locale.US, "%,.2f", stock.fiftyTwoWeekLow)} - $currencySym${String.format(java.util.Locale.US, "%,.2f", stock.fiftyTwoWeekHigh)}"
        binding.tvStatVolume.text = String.format(java.util.Locale.US, "%,d", stock.volume)
        binding.tvStatMarketCap.text = stock.marketCap
        binding.tvStatPeRatio.text = if (stock.peRatio > 0.0) String.format(java.util.Locale.US, "%.1f", stock.peRatio) else "N/A"
        binding.tvStatExchangeCurrency.text = "${stock.exchange} (${stock.currency})"

        // About Company / Description
        binding.tvDetailDescription.text = stock.description

        // Watchlist status icon
        updateBookmarkIcon(stock.isBookmarked)
    }

    private fun updateBookmarkIcon(isBookmarked: Boolean) {
        binding.btnDetailBookmark.setImageResource(
            if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
        )
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDetailBookmark.setOnClickListener {
            val stock = currentStock ?: return@setOnClickListener
            val bookmarked = MarketRepository.toggleBookmark(stock.symbol)
            updateBookmarkIcon(bookmarked)
            val msg = if (bookmarked) "${stock.symbol} added to Watchlist ⭐" else "${stock.symbol} removed from Watchlist"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnDetailShare.setOnClickListener {
            val stock = currentStock ?: return@setOnClickListener
            val shareText = """
                📈 ${stock.name} (${stock.symbol})
                Price: ${stock.formattedPrice} (${stock.formattedChange})
                Exchange: ${stock.exchange} • Sentiment: ${stock.sentiment}
                52W Range: ${stock.currency} ${stock.fiftyTwoWeekLow} - ${stock.fiftyTwoWeekHigh}
                
                Shared via MarketPulse - Real-time Share Market Updates
            """.trimIndent()

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share ${stock.symbol} Market Quote"))
        }

        binding.btnOpenWeb.setOnClickListener {
            val stock = currentStock ?: return@setOnClickListener
            val encodedSymbol = java.net.URLEncoder.encode(stock.symbol, "UTF-8")
            val url = "https://finance.yahoo.com/quote/$encodedSymbol"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open browser", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
