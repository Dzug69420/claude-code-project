package com.dzug.financetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzug.financetracker.data.repository.CategoryRepository
import com.dzug.financetracker.data.repository.TransactionRepository
import com.dzug.financetracker.domain.model.BudgetStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val month: YearMonth = YearMonth.now(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val budgetStatuses: List<BudgetStatus> = emptyList()
) {
    val netBalance: Double get() = totalIncome - totalExpenses
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM")
    private val _month = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<HomeUiState> = _month.flatMapLatest { month ->
        val monthStr = month.format(fmt)
        combine(
            transactionRepo.getTotalIncome(monthStr),
            transactionRepo.getTotalExpenses(monthStr),
            categoryRepo.getExpenseCategoriesFlat(),
            transactionRepo.getCategorySpendingMap(monthStr)
        ) { income, expenses, categories, spending ->
            HomeUiState(
                month = month,
                totalIncome = income ?: 0.0,
                totalExpenses = expenses ?: 0.0,
                budgetStatuses = categories.map { cat ->
                    val spent = spending[cat.id] ?: 0.0
                    val budget = cat.budget ?: 0.0
                    BudgetStatus(
                        category = cat,
                        spent = spent,
                        remaining = budget - spent,
                        percentage = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
                    )
                }.filter { it.hasBudget || it.spent > 0 }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun previousMonth() = _month.update { it.minusMonths(1) }
    fun nextMonth() = _month.update { it.plusMonths(1) }
}
