package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shael.shah.expensemanager.model.Expense;

import java.util.List;

@Dao
public interface ExpenseDao
{

    @Query("SELECT * FROM expense")
    List<Expense> getAllExpenses();

    @Insert
    long insert(Expense expense);

    @Insert
    long[] insertAll(List<Expense> expenses);

    @Update
    int update(Expense expense);

    @Delete
    int delete(Expense expense);

    @Delete
    int deleteAll(List<Expense> expenses);
}