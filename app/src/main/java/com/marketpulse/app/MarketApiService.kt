package com.marketpulse.app

import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object MarketApiService {

    private val executor = Executors.newFixedThreadPool(6)

    /**
     * Fetches real-time stock/index data from Yahoo Finance's public chart API
     * Returns a partial StockItem update or null on error
     */
    fun fetchQuote(symbol: String, timeoutMs: Int = 5000): Pair<Double, Double>? {
        var conn: HttpURLConnection? = null
        try {
            val encodedSymbol = java.net.URLEncoder.encode(symbol, "UTF-8")
            val url = URL("https://query1.finance.yahoo.com/v8/finance/chart/$encodedSymbol?range=1d&interval=15m")
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) MarketPulse/1.0")
                setRequestProperty("Accept", "application/json")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val root = JSONObject(response)
                val chart = root.getJSONObject("chart")
                val results = chart.optJSONArray("result")
                if (results != null && results.length() > 0) {
                    val res0 = results.getJSONObject(0)
                    val meta = res0.getJSONObject("meta")
                    val price = meta.optDouble("regularMarketPrice", 0.0)
                    val changePercent = meta.optDouble("regularMarketChangePercent", 0.0)
                    if (price > 0.0) {
                        return Pair(price, changePercent)
                    }
                }
            }
        } catch (e: Exception) {
            // Silently fall back to cached / fallback values
        } finally {
            conn?.disconnect()
        }
        return null
    }

    /**
     * Fetches breaking share market news from Economic Times RSS feed
     */
    fun fetchBreakingNews(onComplete: (List<MarketNews>) -> Unit) {
        executor.execute {
            val list = mutableListOf<MarketNews>()
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://economictimes.indiatimes.com/markets/rssfeeds/1977021501.cms")
                conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 6000
                    readTimeout = 6000
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) MarketPulse/1.0")
                }

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = conn.inputStream
                    list.addAll(parseRssNews(stream))
                }
            } catch (e: Exception) {
                // Return fallback news below
            } finally {
                conn?.disconnect()
            }

            if (list.isEmpty()) {
                list.addAll(getFallbackNews())
            }

            onComplete(list)
        }
    }

    private fun parseRssNews(inputStream: InputStream): List<MarketNews> {
        val items = mutableListOf<MarketNews>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(inputStream, "UTF-8")

            var eventType = parser.eventType
            var inItem = false
            var title = ""
            var description = ""
            var link = ""
            var pubDate = ""

            while (eventType != XmlPullParser.END_DOCUMENT && items.size < 15) {
                val tagName = parser.name?.lowercase() ?: ""
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "item") inItem = true
                        else if (inItem) {
                            when (tagName) {
                                "title" -> title = parser.nextText().trim()
                                "description" -> description = cleanHtml(parser.nextText().trim())
                                "link" -> link = parser.nextText().trim()
                                "pubdate" -> pubDate = parser.nextText().trim()
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName == "item") {
                            if (title.isNotEmpty()) {
                                items.add(
                                    MarketNews(
                                        id = "news_${items.size + 1}",
                                        title = title,
                                        description = if (description.isNotEmpty()) description else "Tap to read full market coverage.",
                                        source = "Economic Times Markets",
                                        pubDate = if (pubDate.isNotEmpty()) pubDate.take(16) else "Live Update",
                                        url = link,
                                        tag = getTagForTitle(title)
                                    )
                                )
                            }
                            title = ""
                            description = ""
                            link = ""
                            pubDate = ""
                            inItem = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            // Error handling
        }
        return items
    }

    private fun cleanHtml(html: String): String {
        return html.replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .trim()
    }

    private fun getTagForTitle(title: String): String {
        val lower = title.lowercase()
        return when {
            lower.contains("rally") || lower.contains("jump") || lower.contains("surge") || lower.contains("high") -> "Bullish Rally"
            lower.contains("fall") || lower.contains("drop") || lower.contains("crash") || lower.contains("slip") -> "Market Pullback"
            lower.contains("nifty") || lower.contains("sensex") -> "Indices"
            lower.contains("ai") || lower.contains("tech") || lower.contains("chip") -> "Tech & AI"
            lower.contains("gold") || lower.contains("oil") || lower.contains("silver") -> "Commodities"
            lower.contains("bank") || lower.contains("fed") || lower.contains("rbi") || lower.contains("rate") -> "Central Banks"
            else -> "Market Update"
        }
    }

    fun getFallbackNews(): List<MarketNews> {
        return listOf(
            MarketNews(
                id = "fn1",
                title = "Nifty & Sensex Rebound on Strong Domestic Institutional Buying; IT and Auto Lead Gains",
                description = "Indian benchmark indices opened higher tracking positive global cues and robust quarterly earnings expectations across top tech firms.",
                source = "MarketPulse Financial News",
                pubDate = "Today, 09:30 AM",
                url = "https://economictimes.indiatimes.com/markets",
                tag = "Bullish Rally"
            ),
            MarketNews(
                id = "fn2",
                title = "Nvidia Surpasses Major Market Milestones Amid Next-Gen AI Accelerator Demand",
                description = "Enterprise spending on generative AI infrastructure continues to fuel semiconductor valuations, lifting the broader Nasdaq index.",
                source = "MarketPulse Financial News",
                pubDate = "Today, 08:45 AM",
                url = "https://finance.yahoo.com",
                tag = "Tech & AI"
            ),
            MarketNews(
                id = "fn3",
                title = "Gold and Silver Consolidate Ahead of Crucial Inflation Data and Central Bank Commentary",
                description = "Precious metals held key support levels on MCX as currency traders digest macroeconomic indicators and treasury yields.",
                source = "MarketPulse Financial News",
                pubDate = "Today, 07:15 AM",
                url = "https://economictimes.indiatimes.com/markets",
                tag = "Commodities"
            ),
            MarketNews(
                id = "fn4",
                title = "Reliance and HDFC Bank Drive Heavyweight Momentum in Mid-Session Trading",
                description = "Broad-based participation was observed in Nifty 50 constituents, with banking and energy majors recovering from recent consolidation.",
                source = "MarketPulse Financial News",
                pubDate = "Today, 06:00 AM",
                url = "https://economictimes.indiatimes.com/markets",
                tag = "Indices"
            )
        )
    }
}
