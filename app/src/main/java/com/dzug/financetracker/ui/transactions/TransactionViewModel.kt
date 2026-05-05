package com.dzug.financetracker.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzug.financetracker.data.repository.CategoryRepository
import com.dzug.financetracker.data.repository.TransactionRepository
import com.dzug.financetracker.domain.model.ExpenseCategory
import com.dzug.financetracker.domain.model.IncomeCategory
import com.dzug.financetracker.domain.model.Subcategory
import com.dzug.financetracker.domain.model.Transaction
import com.dzug.financetracker.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TransactionListState(
    val month: YearMonth = YearMonth.now(),
    val transactions: List<Transaction> = emptyList()
)

data class AddTransactionFormState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val date: LocalDate = LocalDate.now(),
    val selectedExpenseCategory: ExpenseCategory? = null,
    val selectedIncomeCategory: IncomeCategory? = null,
    val selectedSubcategory: Subcategory? = null,
    val note: String = "",
    val expenseCategories: List<ExpenseCategory> = emptyList(),
    val incomeCategories: List<IncomeCategory> = emptyList(),
    val subcategories: List<Subcategory> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM")
    private val _month = MutableStateFlow(YearMonth.now())

    val listState: StateFlow<TransactionListState> = _month.flatMapLatest { month ->
        transactionRepo.getTransactionsForMonth(month.format(fmt))
            .map { TransactionListState(month = month, transactions = it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionListState())

    private val _formState = MutableStateFlow(AddTransactionFormState())
    val formState: StateFlow<AddTransactionFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                categoryRepo.getExpenseCategories(),
                categoryRepo.getIncomeCategories()
            ) { exp, inc -> exp to inc }
                .collect { (exp, inc) ->
                    _formState.update { it.copy(expenseCategories = exp, incomeCategories = inc) }
                }
        }
    }

    fun previousMonth() = _month.update { it.minusMonths(1) }
    fun nextMonth() = _month.update { it.plusMonths(1) }

    fun setType(type: TransactionType) = _formState.update {
        it.copy(type = type, selectedExpenseCategory = null, selectedIncomeCategory = null, selectedSubcategory = null)
    }
    fun setAmount(v: String) = _formState.update { it.copy(amount = v) }
    fun setDate(d: LocalDate) = _formState.update { it.copy(date = d) }
    fun setNote(v: String) = _formState.update { it.copy(note = v) }

    fun setExpenseCategory(cat: ExpenseCategory) {
        _formState.update { it.copy(selectedExpenseCategory = cat, selectedSubcategory = null, subcategories = cat.subcategories) }
    }

    fun setIncomeCategory(cat: IncomeCategory) {
        _formState.update { it.copy(selectedIncomeCategory = cat) }
    }

    fun setSubcategory(sub: Subcategory?) {
        _formState.update { it.copy(selectedSubcategory = sub) }
    }

    fun saveTransaction() {
        val s = _formState.value
        val amount = s.amount.toDoubleOrNull() ?: return
        val categoryId = if (s.type == TransactionType.EXPENSE)
            s.selectedExpenseCategory?.id ?: return
        else
            s.selectedIncomeCategory?.id ?: return

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            transactionRepo.addTransaction(
                type = s.type,
                amount = amount,
                date = s.date,
                categoryId = categoryId,
                subcategoryId = s.selectedSubcategory?.id,
                note = s.note
            )
            _formState.update { it.copy(isSaving = false, saved = true) }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { transactionRepo.deleteTransaction(transaction) }
    }

    fun resetForm() {
        _formState.update { current ->
            AddTransactionFormState(
                expenseCategories = current.expenseCategories,
                incomeCategories = current.incomeCategories
            )
        }
    }
}
