package com.dzug.financetracker.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzug.financetracker.data.repository.CategoryRepository
import com.dzug.financetracker.data.repository.TransactionRepository
import com.dzug.financetracker.domain.model.BudgetStatus
import com.dzug.financetracker.domain.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BudgetUiState(
    val month: YearMonth = YearMonth.now(),
    val budgetStatuses: List<BudgetStatus> = emptyList()
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM")
    private val _month = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<BudgetUiState> = _month.flatMapLatest { month ->
        val monthStr = month.format(fmt)
        combine(
            categoryRepo.getExpenseCategoriesFlat(),
            transactionRepo.getCategorySpendingMap(monthStr)
        ) { categories, spending ->
            BudgetUiState(
                month = month,
                budgetStatuses = categories.map { cat ->
                    val spent = spending[cat.id] ?: 0.0
                    val budget = cat.budget ?: 0.0
                    BudgetStatus(
                        category = cat,
                        spent = spent,
                        remaining = budget - spent,
                        percentage = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
                    )
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BudgetUiState())

    fun previousMonth() = _month.update { it.minusMonths(1) }
    fun nextMonth() = _month.update { it.plusMonths(1) }

    fun setBudget(category: ExpenseCategory, budget: Double?) {
        viewModelScope.launch {
            categoryRepo.updateExpenseCategory(category.copy(budget = budget))
        }
    }
}
