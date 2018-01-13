package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Query("SELECT * FROM expense")
    List<Expense> getAllExpenses();

    @Insert
    long insert(Expense expense);

    @Insert
    List<Long> insertAll(Expense... expenses);

    @Delete
    int delete(Expense expense);

    @Delete
    int deleteAll(Expense... expenses);
}
