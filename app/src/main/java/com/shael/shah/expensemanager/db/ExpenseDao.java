package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shael.shah.expensemanager.model.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Query("SELECT * FROM expense")
    List<Expense> getAllExpenses();

    @Insert
    void insert(Expense expense);

    @Insert
    void insertAll(List<Expense> expenses);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Delete
    void deleteAll(List<Expense> expenses);
}