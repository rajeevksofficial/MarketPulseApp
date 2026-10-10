package com.marketpulse.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketRepositoryTest {

    @Test
    fun testInitialStocksNotEmpty() {
        val allStocks = MarketRepository.getAllStocks()
        assertTrue("Market stocks list should not be empty", allStocks.isNotEmpty())
        assertTrue("Should contain at least 15 assets", allStocks.size >= 15)
    }

    @Test
    fun testIndicesFilter() {
        val indices = MarketRepository.getIndices()
        assertTrue("Should have global and domestic indices", indices.isNotEmpty())
        for (idx in indices) {
            assertTrue("Item must be marked as an index", idx.isIndex)
        }
    }

    @Test
    fun testTopGainersAndLosers() {
        val gainers = MarketRepository.getTopGainers()
        assertTrue("Should have top gainers", gainers.isNotEmpty())
        for (g in gainers) {
            assertTrue("Gainer change percent must be positive", g.changePercent > 0)
        }

        val losers = MarketRepository.getTopLosers()
        assertTrue("Should have top losers", losers.isNotEmpty())
        for (l in losers) {
            assertTrue("Loser change percent must be negative", l.changePercent < 0)
        }
    }

    @Test
    fun testStockSearchFiltering() {
        val appleSearch = MarketRepository.filterStocks(
            category = "All Assets",
            query = "AAPL",
            watchlistOnly = false
        )
        assertEquals(1, appleSearch.size)
        assertEquals("AAPL", appleSearch[0].symbol)

        val nvidiaSearch = MarketRepository.filterStocks(
            category = "All Assets",
            query = "NVIDIA",
            watchlistOnly = false
        )
        assertEquals(1, nvidiaSearch.size)
        assertEquals("NVDA", nvidiaSearch[0].symbol)
    }

    @Test
    fun testCategoryFiltering() {
        val indianEquities = MarketRepository.filterStocks(
            category = "🇮🇳 Indian Equities",
            query = "",
            watchlistOnly = false
        )
        assertTrue("Should have Indian equities", indianEquities.isNotEmpty())
        for (item in indianEquities) {
            assertEquals("Indian Equities", item.category)
        }

        val usTech = MarketRepository.filterStocks(
            category = "🇺🇸 US Tech Giants",
            query = "",
            watchlistOnly = false
        )
        assertTrue("Should have US Tech stocks", usTech.isNotEmpty())
        for (item in usTech) {
            assertEquals("US Tech", item.category)
        }
    }

    @Test
    fun testWatchlistToggle() {
        val symbol = "AAPL"
        val itemBefore = MarketRepository.getStockBySymbol(symbol)
        assertNotNull(itemBefore)
        val initialStatus = itemBefore!!.isBookmarked

        val newStatus = MarketRepository.toggleBookmark(symbol)
        assertEquals(!initialStatus, newStatus)

        // Filter by watchlist
        val watchlistItems = MarketRepository.filterStocks(
            category = "⭐ Watchlist",
            query = "",
            watchlistOnly = true
        )
        assertTrue("Watchlist should reflect toggled item", watchlistItems.any { it.symbol == symbol })

        // Toggle back to clean up
        MarketRepository.toggleBookmark(symbol)
    }

    @Test
    fun testStockFormatting() {
        val inrStock = StockItem(
            symbol = "TEST.NS",
            name = "Test Stock",
            exchange = "NSE",
            category = "Indian Equities",
            price = 2500.50,
            change = 25.50,
            changePercent = 1.03,
            currency = "INR"
        )
        assertEquals("₹2,500.50", inrStock.formattedPrice)
        assertTrue(inrStock.formattedChange.contains("+₹25.50"))
        assertTrue(inrStock.isPositive)

        val usdStock = StockItem(
            symbol = "TEST",
            name = "Test Stock US",
            exchange = "NASDAQ",
            category = "US Tech",
            price = 150.25,
            change = -3.25,
            changePercent = -2.12,
            currency = "USD"
        )
        assertEquals("$150.25", usdStock.formattedPrice)
        assertFalse(usdStock.isPositive)
    }
}
