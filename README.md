# MarketPulse 📈

A modern, native Android financial intelligence and real-time share market tracking application built with Kotlin. **MarketPulse** delivers live stock market indices, company valuation metrics, commodity quotes, breaking financial news, and interactive sparkline charts—powered entirely by open-source, free REST and RSS endpoints without requiring paid API keys or authentication.

---

## 🌟 Key Features

- **Free Open-Source Market Ingestion**:
  - Intraday price data and quotes via public Yahoo Finance Chart API (`/v8/finance/chart/{symbol}?range=1d&interval=15m`).
  - Real-time financial headlines and commentary from The Economic Times Markets RSS feed.
  - Offline-first dual-engine fallback covering 26 major equities across NSE, BSE, NASDAQ, and NYSE.
- **Hardware-Accelerated Custom Canvas Sparklines**:
  - Custom `SparklineView` drawing smooth cubic Bezier curves (`cubicTo`) and soft vertical gradient fills.
  - Bullish (emerald `#10B981`) and bearish (crimson `#EF4444`) dynamic coloration.
- **High-Performance UI Architecture**:
  - Powered by `ConcatAdapter` combining market sentiment banner, indices ticker, breaking news, and recycled equities cards.
  - 60–120 FPS buttery smooth scrolling with zero dropped frames.
- **Deep Valuation & Stock Detail Screen**:
  - Interactive 180dp expanded chart.
  - 6-metric valuation grid: Day Range, 52-Week Range, Volume, Market Cap, P/E Ratio, Exchange/Currency.
  - Company profile summary, system share sheet, and direct links to live web analytics.
- **Personalization & Search**:
  - 150ms debounced search bar.
  - Category filters: *All Assets*, *Top Gainers*, *Top Losers*, *Indian Equities*, *US Tech Giants*, *Market Indices*, *Commodities*, and *Watchlist*.
  - Persistent local watchlist saved via `SharedPreferences`.

---

## 🏗️ Architecture & Project Structure

```text
MarketPulseApp/
├── app/
│   ├── src/main/java/com/marketpulse/app/
│   │   ├── MainActivity.kt               # Main screen, search, category chips, ConcatAdapter
│   │   ├── StockDetailActivity.kt        # Deep valuation view & large interactive chart
│   │   ├── MarketApiService.kt           # Open-source API caller & RSS XML stream parser
│   │   ├── MarketRepository.kt           # Cache, concurrency, and resilient fallback engine
│   │   ├── SparklineView.kt              # Custom View: Bezier curve & gradient Canvas rendering
│   │   ├── StockItem.kt                  # Asset data model
│   │   ├── MarketNews.kt                 # Financial news data model
│   │   ├── StockListAdapter.kt           # Equities RecyclerView adapter with DiffUtil
│   │   ├── MarketHomeHeaderAdapter.kt    # Sentiment banner, indices, and news header
│   │   ├── MarketIndexAdapter.kt         # Horizontal indices ticker
│   │   └── MarketNewsAdapter.kt          # Horizontal news carousel adapter
│   └── src/main/res/
│       ├── layout/                       # Clean XML layouts
│       └── values/                       # Dark financial theme & colors
├── build.gradle.kts                      # Gradle build script (compileSdk 36, minSdk 24)
└── MarketPulseApp.apk                    # Pre-compiled ready-to-run APK (5.85 MB)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- JDK 17 or JDK 21
- Android SDK with API 36

### Build & Run
Clone the repository and build using Gradle:

```bash
git clone https://github.com/rajeevksofficial/MarketPulseApp.git
cd MarketPulseApp
./gradlew assembleDebug
```

### Install APK Directly
To install the pre-built APK onto a connected Android device or emulator:

```bash
adb install -r MarketPulseApp.apk
adb shell am start -n com.marketpulse.app/.MainActivity
```

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).
