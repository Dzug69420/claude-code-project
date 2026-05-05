package com.dzug.financetracker.di

import android.content.Context
import androidx.room.Room
import com.dzug.financetracker.data.db.AppDatabase
import com.dzug.financetracker.data.db.DatabaseCallback
import com.dzug.financetracker.data.db.dao.ExpenseCategoryDao
import com.dzug.financetracker.data.db.dao.IncomeCategoryDao
import com.dzug.financetracker.data.db.dao.SubcategoryDao
import com.dzug.financetracker.data.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "finance_tracker.db")
            .addCallback(callback)
            .build()

    @Provides fun provideExpenseCategoryDao(db: AppDatabase): ExpenseCategoryDao = db.expenseCategoryDao()
    @Provides fun provideSubcategoryDao(db: AppDatabase): SubcategoryDao = db.subcategoryDao()
    @Provides fun provideIncomeCategoryDao(db: AppDatabase): IncomeCategoryDao = db.incomeCategoryDao()
    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
}
