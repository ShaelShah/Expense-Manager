package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shael.shah.expensemanager.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Insert
    long insert(Category category);

    @Insert
    long[] insert(List<Category> categories);

    @Update
    int update(Category category);

    @Update
    int update(List<Category> categories);

    @Delete
    int delete(Category category);

    @Delete
    int delete(List<Category> categories);
}