package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;

@Database(entities = {Expense.class, Income.class, Category.class}, version = 8)
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public abstract class ApplicationDatabase extends RoomDatabase {

    private static ApplicationDatabase instance;

    public static ApplicationDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), ApplicationDatabase.class, "expense-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        return instance;
    }

    public abstract ExpenseDao expenseDao();

    public abstract IncomeDao incomeDao();

    public abstract CategoryDao categoryDao();

    public void destroyInstance() {
        instance = null;
    }
}