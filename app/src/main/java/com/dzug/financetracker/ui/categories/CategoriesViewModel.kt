package com.dzug.financetracker.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzug.financetracker.data.repository.CategoryRepository
import com.dzug.financetracker.domain.model.ExpenseCategory
import com.dzug.financetracker.domain.model.IncomeCategory
import com.dzug.financetracker.domain.model.Subcategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val expenseCategories: List<ExpenseCategory> = emptyList(),
    val incomeCategories: List<IncomeCategory> = emptyList()
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = combine(
        categoryRepo.getExpenseCategories(),
        categoryRepo.getIncomeCategories()
    ) { exp, inc ->
        CategoriesUiState(expenseCategories = exp, incomeCategories = inc)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

    fun addExpenseCategory(name: String, icon: String, color: Long, budget: Double?) {
        if (name.isBlank()) return
        viewModelScope.launch { categoryRepo.addExpenseCategory(name, icon, color, budget) }
    }

    fun updateExpenseCategory(category: ExpenseCategory) {
        viewModelScope.launch { categoryRepo.updateExpenseCategory(category) }
    }

    fun deleteExpenseCategory(category: ExpenseCategory) {
        viewModelScope.launch { categoryRepo.deleteExpenseCategory(category) }
    }

    fun addSubcategory(name: String, categoryId: Long) {
        if (name.isBlank()) return
        viewModelScope.launch { categoryRepo.addSubcategory(name, categoryId) }
    }

    fun deleteSubcategory(sub: Subcategory) {
        viewModelScope.launch { categoryRepo.deleteSubcategory(sub) }
    }

    fun addIncomeCategory(name: String, icon: String, color: Long) {
        if (name.isBlank()) return
        viewModelScope.launch { categoryRepo.addIncomeCategory(name, icon, color) }
    }

    fun deleteIncomeCategory(category: IncomeCategory) {
        viewModelScope.launch { categoryRepo.deleteIncomeCategory(category) }
    }
}
