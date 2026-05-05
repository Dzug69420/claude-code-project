# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Is

A native Android finance tracking app built with Kotlin + Jetpack Compose. Tracks monthly income and expenses, supports category/subcategory structure, budget limits per expense category, and shows remaining budget in real time.

**GitHub:** https://github.com/Dzug69420/claude-code-project

## Build Commands

```bash
./gradlew assembleDebug        # build debug APK
./gradlew assembleRelease      # build release APK
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests (requires emulator/device)
./gradlew lint                 # lint checks
```

On Windows use `gradlew.bat` instead of `./gradlew`.

> **First-time setup:** Open the project in Android Studio — it will automatically download the Gradle wrapper jar and Android SDK components. The `gradle/wrapper/gradle-wrapper.jar` binary is not tracked in git; Android Studio provides it.

## Architecture

**Pattern:** MVVM + Repository, single-module Android app.

**Package:** `com.dzug.financetracker`

```
data/
  db/          — Room database, entities, DAOs, DatabaseCallback (seeds defaults on first launch)
  repository/  — CategoryRepository, TransactionRepository (expose Flow<> to ViewModels)
domain/model/  — Plain Kotlin data classes: ExpenseCategory, Subcategory, IncomeCategory,
                 Transaction, BudgetStatus, TransactionType
di/            — Hilt DatabaseModule (provides DB + all DAOs)
ui/
  theme/       — Material3 theme (Color.kt, Type.kt, Theme.kt)
  navigation/  — Screen sealed class, AppNavigation composable (Scaffold + NavHost + bottom bar)
  home/        — Monthly summary + budget progress bars
  transactions/— Transaction list (grouped by day) + Add/Edit screen
  budget/      — Per-category budget cards with edit dialog
  categories/  — Full CRUD for expense categories, subcategories, income categories
  components/  — BudgetProgressCard, MonthSelector, TransactionItem (shared)
```

## Key Data Flow

- `TransactionRepository` / `CategoryRepository` expose `Flow<>` from Room DAOs
- Each `@HiltViewModel` collects via `combine()` → emits `StateFlow<UiState>`
- Composables observe via `collectAsStateWithLifecycle()`
- Month navigation uses a `MutableStateFlow<YearMonth>` + `flatMapLatest` to re-query

## Database

Room DB (`finance_tracker.db`), no migrations (version 1). `DatabaseCallback` seeds default categories on first launch. Colors stored as `Long` (e.g. `0xFFE57373`); use `Color(colorLong.toInt())` in Compose. Transaction dates stored as `LocalDate.toEpochDay()` (Long).

## Stack Versions

| Tool | Version |
|---|---|
| Kotlin | 1.9.22 |
| AGP | 8.2.2 |
| Compose BOM | 2024.02.00 |
| Compose Compiler | 1.5.8 |
| Room | 2.6.1 |
| Hilt | 2.50 |
| Navigation Compose | 2.7.7 |
| minSdk | 26 |
| targetSdk | 34 |

## Git Workflow

Commit and push after every meaningful change. Use clear, descriptive commit messages.
