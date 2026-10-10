package com.marketpulse.app

data class StockItem(
    val symbol: String,
    val name: String,
    val exchange: String,
    val category: String,
    var price: Double,
    var change: Double,
    var changePercent: Double,
    val currency: String = "USD",
    var dayHigh: Double = 0.0,
    var dayLow: Double = 0.0,
    var fiftyTwoWeekHigh: Double = 0.0,
    var fiftyTwoWeekLow: Double = 0.0,
    var volume: Long = 0L,
    val marketCap: String = "N/A",
    val peRatio: Double = 0.0,
    val sentiment: String = "Bullish",
    val description: String = "",
    var historicalPoints: List<Float> = emptyList(),
    var isBookmarked: Boolean = false,
    val isIndex: Boolean = false,
    var lastUpdated: Long = System.currentTimeMillis()
) {
    val isPositive: Boolean
        get() = change >= 0.0

    val formattedPrice: String
        get() {
            val symbolChar = if (currency == "INR") "₹" else "$"
            return "$symbolChar${String.format(java.util.Locale.US, "%,.2f", price)}"
        }

    val formattedChange: String
        get() {
            val sign = if (change >= 0) "+" else ""
            val symbolChar = if (currency == "INR") "₹" else "$"
            return "$sign$symbolChar${String.format(java.util.Locale.US, "%.2f", change)} ($sign${String.format(java.util.Locale.US, "%.2f", changePercent)}%)"
        }
}
