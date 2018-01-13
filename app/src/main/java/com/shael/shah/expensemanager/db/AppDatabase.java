package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Expense.class, Category.class}, version = 2)
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public abstract class AppDatabase extends RoomDatabase{

    public abstract ExpenseDao expenseDao();
    public abstract CategoryDao categoryDao();

    private static AppDatabase instance;

    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "expense-database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}
