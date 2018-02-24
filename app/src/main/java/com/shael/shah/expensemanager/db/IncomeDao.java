package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shael.shah.expensemanager.model.Income;

import java.util.List;

@Dao
public interface IncomeDao {

    @Query("SELECT * FROM income")
    List<Income> getAllIncomes();

    @Insert
    long insert(Income income);

    @Update
    int update(Income income);

    @Delete
    int delete(Income income);
}