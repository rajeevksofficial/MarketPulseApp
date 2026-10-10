package com.marketpulse.app

data class MarketNews(
    val id: String,
    val title: String,
    val description: String,
    val source: String,
    val pubDate: String,
    val url: String,
    val tag: String
)
