package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;

@Database(entities = {Expense.class, Category.class}, version = 4)
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public abstract class ApplicationDatabase extends RoomDatabase {

    public abstract ExpenseDao expenseDao();
    public abstract CategoryDao categoryDao();

    private static ApplicationDatabase instance;

    public static ApplicationDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), ApplicationDatabase.class, "expense-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        return instance;
    }

    public void destroyInstance() {
        instance = null;
    }
}