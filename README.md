# Webshop

An Android e-commerce application built with Java. Users can browse products, manage a shopping cart, and check out, while admins can manage the product catalog.

## Features

- **Product Browsing** – Grid layout showing product name, price, image, and stock
- **Shopping Cart** – Add items, adjust quantities (capped by stock), and checkout
- **Admin Panel** – Create, edit, and delete products (admin accounts only)
- **User Authentication** – Login with username and password
- **Inventory Tracking** – Stock levels updated in real time

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| Platform | Android (min SDK 26 / target SDK 34) |
| UI | Material Design, ConstraintLayout, RecyclerView |
| Networking | Retrofit 2 + Gson |
| Build | Gradle (Kotlin DSL) |
| Testing | JUnit 4, Espresso |

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android device or emulator running Android 8.0+
- A running backend API (default: `http://192.168.32.85:8080/api/`)

### Setup

1. Clone the repository and open it in Android Studio.
2. Update the base URL in `MainActivity.java` to point to your backend.
3. Run the app on a device or emulator:

```bash
./gradlew installDebug
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented (UI) tests – requires a connected device or emulator
./gradlew connectedAndroidTest
```
