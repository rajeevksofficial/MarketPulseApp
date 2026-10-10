package com.marketpulse.app

import java.util.concurrent.Executors
import kotlin.concurrent.thread

object MarketRepository {

    private val executor = Executors.newFixedThreadPool(4)

    private val initialStocks = mutableListOf(
        // === INDICES ===
        StockItem(
            symbol = "^NSEI",
            name = "NIFTY 50",
            exchange = "NSE",
            category = "Indices",
            price = 22520.45,
            change = 288.65,
            changePercent = 1.30,
            currency = "INR",
            dayHigh = 22580.75,
            dayLow = 22294.75,
            fiftyTwoWeekHigh = 26373.20,
            fiftyTwoWeekLow = 22179.90,
            volume = 245000000L,
            marketCap = "₹220 Lakh Cr",
            peRatio = 22.8,
            sentiment = "Bullish",
            description = "The flagship benchmark index of the National Stock Exchange of India, representing 50 of the largest Indian companies across key economic sectors.",
            historicalPoints = listOf(22316f, 22375f, 22429f, 22460f, 22480f, 22509f, 22510f, 22496f, 22509f, 22548f, 22562f, 22520f),
            isIndex = true
        ),
        StockItem(
            symbol = "^BSESN",
            name = "BSE SENSEX",
            exchange = "BSE",
            category = "Indices",
            price = 74119.30,
            change = 890.40,
            changePercent = 1.22,
            currency = "INR",
            dayHigh = 74350.00,
            dayLow = 73450.20,
            fiftyTwoWeekHigh = 85978.25,
            fiftyTwoWeekLow = 71200.00,
            volume = 120000000L,
            marketCap = "₹410 Lakh Cr",
            peRatio = 23.5,
            sentiment = "Bullish",
            description = "The 30-stock benchmark index of the Bombay Stock Exchange, the oldest stock exchange in Asia, tracking India's industrial and financial giants.",
            historicalPoints = listOf(73450f, 73620f, 73800f, 73920f, 74100f, 74250f, 74150f, 74200f, 74300f, 74119f),
            isIndex = true
        ),
        StockItem(
            symbol = "^GSPC",
            name = "S&P 500",
            exchange = "INDEX",
            category = "Indices",
            price = 5751.13,
            change = 48.20,
            changePercent = 0.85,
            currency = "USD",
            dayHigh = 5760.40,
            dayLow = 5712.10,
            fiftyTwoWeekHigh = 5878.50,
            fiftyTwoWeekLow = 4103.78,
            volume = 3850000000L,
            marketCap = "$46.2 T",
            peRatio = 26.4,
            sentiment = "Bullish",
            description = "Standard & Poor's 500 tracks the performance of 500 of the largest companies listed on stock exchanges in the United States.",
            historicalPoints = listOf(5712f, 5725f, 5732f, 5740f, 5748f, 5755f, 5760f, 5758f, 5751f),
            isIndex = true
        ),
        StockItem(
            symbol = "^IXIC",
            name = "NASDAQ Composite",
            exchange = "NASDAQ",
            category = "Indices",
            price = 18137.85,
            change = 215.40,
            changePercent = 1.20,
            currency = "USD",
            dayHigh = 18210.00,
            dayLow = 17980.50,
            fiftyTwoWeekHigh = 18671.07,
            fiftyTwoWeekLow = 12543.86,
            volume = 5120000000L,
            marketCap = "$28.4 T",
            peRatio = 34.2,
            sentiment = "Strong Buy",
            description = "Technology-heavy index tracking over 3,000 common equities listed on the Nasdaq stock market, led by mega-cap software, hardware, and AI firms.",
            historicalPoints = listOf(17980f, 18020f, 18070f, 18110f, 18150f, 18200f, 18180f, 18137f),
            isIndex = true
        ),

        // === INDIAN EQUITIES (NSE) ===
        StockItem(
            symbol = "RELIANCE.NS",
            name = "Reliance Industries Ltd",
            exchange = "NSE",
            category = "Indian Equities",
            price = 2814.50,
            change = 42.15,
            changePercent = 1.52,
            currency = "INR",
            dayHigh = 2835.00,
            dayLow = 2780.00,
            fiftyTwoWeekHigh = 3217.90,
            fiftyTwoWeekLow = 2220.30,
            volume = 8245100L,
            marketCap = "₹19.0 Lakh Cr",
            peRatio = 27.2,
            sentiment = "Bullish",
            description = "India's largest conglomerate by market capitalization, with core leadership in petrochemicals, oil refining, telecom (Jio), and nationwide retail.",
            historicalPoints = listOf(2780f, 2792f, 2805f, 2815f, 2828f, 2835f, 2822f, 2814f)
        ),
        StockItem(
            symbol = "TCS.NS",
            name = "Tata Consultancy Services",
            exchange = "NSE",
            category = "Indian Equities",
            price = 4210.80,
            change = 65.40,
            changePercent = 1.58,
            currency = "INR",
            dayHigh = 4235.00,
            dayLow = 4160.00,
            fiftyTwoWeekHigh = 4585.90,
            fiftyTwoWeekLow = 3313.00,
            volume = 2154300L,
            marketCap = "₹15.2 Lakh Cr",
            peRatio = 31.4,
            sentiment = "Bullish",
            description = "Global IT services and consulting powerhouse, part of the Tata Group, driving enterprise digital transformation, AI platforms, and cloud adoption worldwide.",
            historicalPoints = listOf(4160f, 4175f, 4190f, 4215f, 4225f, 4235f, 4220f, 4210f)
        ),
        StockItem(
            symbol = "HDFCBANK.NS",
            name = "HDFC Bank Limited",
            exchange = "NSE",
            category = "Indian Equities",
            price = 1675.20,
            change = 18.50,
            changePercent = 1.12,
            currency = "INR",
            dayHigh = 1688.00,
            dayLow = 1658.00,
            fiftyTwoWeekHigh = 1794.00,
            fiftyTwoWeekLow = 1363.55,
            volume = 14320000L,
            marketCap = "₹12.7 Lakh Cr",
            peRatio = 18.9,
            sentiment = "Moderate Buy",
            description = "India's largest private sector bank, delivering banking, retail credit, wealth management, and treasury solutions across thousands of urban and rural branches.",
            historicalPoints = listOf(1658f, 1664f, 1670f, 1678f, 1685f, 1688f, 1680f, 1675f)
        ),
        StockItem(
            symbol = "INFY.NS",
            name = "Infosys Limited",
            exchange = "NSE",
            category = "Indian Equities",
            price = 1912.40,
            change = -14.80,
            changePercent = -0.77,
            currency = "INR",
            dayHigh = 1935.00,
            dayLow = 1905.00,
            fiftyTwoWeekHigh = 1991.45,
            fiftyTwoWeekLow = 1358.35,
            volume = 6542100L,
            marketCap = "₹7.9 Lakh Cr",
            peRatio = 29.8,
            sentiment = "Neutral",
            description = "Pioneer in modern IT consulting and next-generation digital services, renowned for enterprise AI suites (Topaz) and cloud architecture.",
            historicalPoints = listOf(1935f, 1930f, 1924f, 1918f, 1910f, 1905f, 1908f, 1912f)
        ),
        StockItem(
            symbol = "TATAMOTORS.NS",
            name = "Tata Motors Limited",
            exchange = "NSE",
            category = "Indian Equities",
            price = 932.60,
            change = 28.30,
            changePercent = 3.13,
            currency = "INR",
            dayHigh = 942.00,
            dayLow = 908.50,
            fiftyTwoWeekHigh = 1179.05,
            fiftyTwoWeekLow = 621.75,
            volume = 12450000L,
            marketCap = "₹3.4 Lakh Cr",
            peRatio = 11.2,
            sentiment = "Strong Buy",
            description = "Leading automotive manufacturer in commercial and passenger vehicles, spearheading India's electric vehicle transition and luxury SUVs via Jaguar Land Rover (JLR).",
            historicalPoints = listOf(908f, 915f, 922f, 928f, 936f, 942f, 938f, 932f)
        ),
        StockItem(
            symbol = "ICICIBANK.NS",
            name = "ICICI Bank Limited",
            exchange = "NSE",
            category = "Indian Equities",
            price = 1245.10,
            change = 14.20,
            changePercent = 1.15,
            currency = "INR",
            dayHigh = 1252.00,
            dayLow = 1232.00,
            fiftyTwoWeekHigh = 1334.00,
            fiftyTwoWeekLow = 898.85,
            volume = 9800000L,
            marketCap = "₹8.8 Lakh Cr",
            peRatio = 17.6,
            sentiment = "Bullish",
            description = "Major private financial services institution offering diversified commercial banking, retail financing, mortgages, and digital banking platforms.",
            historicalPoints = listOf(1232f, 1238f, 1242f, 1248f, 1252f, 1249f, 1245f)
        ),

        // === US TECH GIANTS ===
        StockItem(
            symbol = "NVDA",
            name = "NVIDIA Corporation",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 134.80,
            change = 5.20,
            changePercent = 4.01,
            currency = "USD",
            dayHigh = 136.15,
            dayLow = 130.20,
            fiftyTwoWeekHigh = 140.76,
            fiftyTwoWeekLow = 39.23,
            volume = 68420000L,
            marketCap = "$3.31 T",
            peRatio = 54.2,
            sentiment = "Strong Buy",
            description = "Pioneer of GPU-accelerated computing and the undisputed market leader in AI data center hardware, CUDA software, and Blackwell architectures.",
            historicalPoints = listOf(130.2f, 131.5f, 133.0f, 134.2f, 135.5f, 136.1f, 135.2f, 134.8f)
        ),
        StockItem(
            symbol = "AAPL",
            name = "Apple Inc.",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 228.45,
            change = 3.15,
            changePercent = 1.40,
            currency = "USD",
            dayHigh = 229.80,
            dayLow = 225.60,
            fiftyTwoWeekHigh = 237.23,
            fiftyTwoWeekLow = 164.08,
            volume = 48210000L,
            marketCap = "$3.47 T",
            peRatio = 33.8,
            sentiment = "Bullish",
            description = "World's most valuable consumer technology company, creating iPhones, Mac, iPad, Apple Silicon chips, and an integrated services ecosystem.",
            historicalPoints = listOf(225.6f, 226.4f, 227.1f, 228.0f, 229.2f, 229.8f, 229.0f, 228.4f)
        ),
        StockItem(
            symbol = "MSFT",
            name = "Microsoft Corporation",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 422.30,
            change = 4.60,
            changePercent = 1.10,
            currency = "USD",
            dayHigh = 424.80,
            dayLow = 418.50,
            fiftyTwoWeekHigh = 468.35,
            fiftyTwoWeekLow = 327.00,
            volume = 21450000L,
            marketCap = "$3.14 T",
            peRatio = 35.1,
            sentiment = "Bullish",
            description = "Global software and cloud infrastructure leader, powering enterprise workflows through Azure Cloud, Microsoft 365, Copilot, and Windows.",
            historicalPoints = listOf(418.5f, 420.0f, 421.2f, 422.8f, 424.5f, 424.8f, 423.5f, 422.3f)
        ),
        StockItem(
            symbol = "GOOGL",
            name = "Alphabet Inc.",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 168.20,
            change = -2.10,
            changePercent = -1.23,
            currency = "USD",
            dayHigh = 171.40,
            dayLow = 167.50,
            fiftyTwoWeekHigh = 191.75,
            fiftyTwoWeekLow = 120.21,
            volume = 24100000L,
            marketCap = "$2.08 T",
            peRatio = 23.4,
            sentiment = "Neutral",
            description = "Parent company of Google, dominating global web search, YouTube streaming, Google Cloud, Android OS, and Gemini AI research.",
            historicalPoints = listOf(171.4f, 170.8f, 169.5f, 168.9f, 167.5f, 168.0f, 168.2f)
        ),
        StockItem(
            symbol = "AMZN",
            name = "Amazon.com Inc.",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 186.50,
            change = 2.80,
            changePercent = 1.52,
            currency = "USD",
            dayHigh = 188.00,
            dayLow = 184.20,
            fiftyTwoWeekHigh = 201.20,
            fiftyTwoWeekLow = 118.35,
            volume = 38900000L,
            marketCap = "$1.95 T",
            peRatio = 43.1,
            sentiment = "Bullish",
            description = "E-commerce and cloud computing giant (AWS), delivering global logistics, streaming media (Prime Video), and scalable web architectures.",
            historicalPoints = listOf(184.2f, 185.1f, 186.0f, 187.2f, 188.0f, 187.5f, 186.5f)
        ),
        StockItem(
            symbol = "TSLA",
            name = "Tesla Inc.",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 238.70,
            change = -6.40,
            changePercent = -2.61,
            currency = "USD",
            dayHigh = 246.50,
            dayLow = 236.10,
            fiftyTwoWeekHigh = 271.00,
            fiftyTwoWeekLow = 138.80,
            volume = 84500000L,
            marketCap = "$760 B",
            peRatio = 62.8,
            sentiment = "Bearish",
            description = "Electric vehicle and clean energy leader, advancing autonomous driving (FSD), Megapack grid energy storage, and humanoid robotics (Optimus).",
            historicalPoints = listOf(246.5f, 244.2f, 241.0f, 238.5f, 236.1f, 237.5f, 238.7f)
        ),
        StockItem(
            symbol = "META",
            name = "Meta Platforms Inc.",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 584.20,
            change = 12.80,
            changePercent = 2.24,
            currency = "USD",
            dayHigh = 588.50,
            dayLow = 573.00,
            fiftyTwoWeekHigh = 602.95,
            fiftyTwoWeekLow = 279.40,
            volume = 14200000L,
            marketCap = "$1.48 T",
            peRatio = 28.5,
            sentiment = "Strong Buy",
            description = "Social technology innovator connecting billions via Facebook, Instagram, WhatsApp, and advancing open-source AI models (Llama).",
            historicalPoints = listOf(573f, 576f, 580f, 583f, 587f, 588.5f, 584.2f)
        ),

        // === COMMODITIES & FOREX ===
        StockItem(
            symbol = "GC=F",
            name = "Gold Futures",
            exchange = "COMMODITY",
            category = "Commodities",
            price = 2658.40,
            change = 18.60,
            changePercent = 0.70,
            currency = "USD",
            dayHigh = 2672.00,
            dayLow = 2645.00,
            fiftyTwoWeekHigh = 2694.90,
            fiftyTwoWeekLow = 1823.50,
            volume = 182000L,
            marketCap = "Global Asset",
            peRatio = 0.0,
            sentiment = "Bullish",
            description = "The world's foremost safe-haven store of value, heavily influenced by global interest rate trajectories, geopolitical shifts, and central bank reserves.",
            historicalPoints = listOf(2645f, 2650f, 2656f, 2664f, 2672f, 2665f, 2658f)
        ),
        StockItem(
            symbol = "SI=F",
            name = "Silver Futures",
            exchange = "COMMODITY",
            category = "Commodities",
            price = 31.85,
            change = 0.62,
            changePercent = 1.98,
            currency = "USD",
            dayHigh = 32.20,
            dayLow = 31.10,
            fiftyTwoWeekHigh = 32.98,
            fiftyTwoWeekLow = 20.69,
            volume = 94000L,
            marketCap = "Global Asset",
            peRatio = 0.0,
            sentiment = "Bullish",
            description = "Dual-purpose precious and industrial metal essential for photovoltaic solar cells, electronics, medical applications, and physical bullion.",
            historicalPoints = listOf(31.1f, 31.3f, 31.6f, 31.9f, 32.2f, 32.0f, 31.85f)
        ),
        StockItem(
            symbol = "CL=F",
            name = "Crude Oil (WTI)",
            exchange = "COMMODITY",
            category = "Commodities",
            price = 75.30,
            change = -1.45,
            changePercent = -1.89,
            currency = "USD",
            dayHigh = 77.10,
            dayLow = 74.80,
            fiftyTwoWeekHigh = 90.78,
            fiftyTwoWeekLow = 65.27,
            volume = 320000L,
            marketCap = "Global Asset",
            peRatio = 0.0,
            sentiment = "Bearish",
            description = "West Texas Intermediate benchmark crude, reflecting worldwide energy demand, refinery throughput, and OPEC+ production policies.",
            historicalPoints = listOf(77.1f, 76.5f, 75.8f, 75.1f, 74.8f, 75.0f, 75.3f)
        )
    )

    private val stockList = mutableListOf<StockItem>().apply {
        addAll(initialStocks)
    }

    private var latestNews = mutableListOf<MarketNews>().apply {
        addAll(MarketApiService.getFallbackNews())
    }

    @Volatile
    private var isRefreshing = false

    fun getAllStocks(): List<StockItem> = synchronized(this) { stockList.toList() }

    fun getIndices(): List<StockItem> = synchronized(this) { stockList.filter { it.isIndex } }

    fun getTopGainers(): List<StockItem> = synchronized(this) {
        stockList.filter { !it.isIndex && it.changePercent > 0 }
            .sortedByDescending { it.changePercent }
    }

    fun getTopLosers(): List<StockItem> = synchronized(this) {
        stockList.filter { !it.isIndex && it.changePercent < 0 }
            .sortedBy { it.changePercent }
    }

    fun getStockBySymbol(symbol: String): StockItem? = synchronized(this) {
        stockList.find { it.symbol.equals(symbol, ignoreCase = true) }
    }

    fun toggleBookmark(symbol: String): Boolean = synchronized(this) {
        val item = getStockBySymbol(symbol) ?: return false
        item.isBookmarked = !item.isBookmarked
        item.isBookmarked
    }

    fun getLatestNews(): List<MarketNews> = synchronized(this) { latestNews.toList() }

    fun filterStocks(category: String, query: String, watchlistOnly: Boolean): List<StockItem> = synchronized(this) {
        stockList.filter { item ->
            val matchesWatchlist = !watchlistOnly || item.isBookmarked

            val matchesCategory = when (category) {
                "All Assets", "All" -> true
                "🚀 Top Gainers" -> item.changePercent > 0
                "📉 Top Losers" -> item.changePercent < 0
                "🇮🇳 Indian Equities" -> item.category == "Indian Equities"
                "🇺🇸 US Tech Giants" -> item.category == "US Tech"
                "📊 Market Indices" -> item.isIndex
                "🪙 Commodities" -> item.category == "Commodities"
                "⭐ Watchlist" -> item.isBookmarked
                else -> item.category.equals(category, ignoreCase = true)
            }

            val matchesQuery = query.isBlank() ||
                    item.symbol.contains(query, ignoreCase = true) ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.exchange.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)

            matchesWatchlist && matchesCategory && matchesQuery
        }
    }

    /**
     * Refreshes market data asynchronously using open Yahoo Finance endpoints
     * and live news feeds.
     */
    fun refreshMarketData(onComplete: (Boolean) -> Unit) {
        if (isRefreshing) {
            onComplete(false)
            return
        }
        isRefreshing = true

        executor.execute {
            var anyUpdated = false

            // Query live quotes for active symbols
            val symbolsToQuery = listOf("^NSEI", "^BSESN", "^GSPC", "^IXIC", "AAPL", "NVDA", "MSFT", "RELIANCE.NS", "TCS.NS")
            for (sym in symbolsToQuery) {
                val live = MarketApiService.fetchQuote(sym, timeoutMs = 2500)
                if (live != null) {
                    val (livePrice, liveChangePct) = live
                    synchronized(this) {
                        val stock = stockList.find { it.symbol == sym }
                        if (stock != null) {
                            val oldPrice = stock.price
                            stock.price = livePrice
                            stock.changePercent = liveChangePct
                            stock.change = livePrice - (livePrice / (1 + (liveChangePct / 100.0)))
                            stock.lastUpdated = System.currentTimeMillis()
                            anyUpdated = true
                        }
                    }
                }
            }

            // Also refresh breaking news
            MarketApiService.fetchBreakingNews { newsList ->
                synchronized(this) {
                    if (newsList.isNotEmpty()) {
                        latestNews.clear()
                        latestNews.addAll(newsList)
                    }
                }
                isRefreshing = false
                onComplete(anyUpdated || newsList.isNotEmpty())
            }
        }
    }
}
