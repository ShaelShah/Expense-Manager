package com.shael.shah.expensemanager.db;

import android.arch.persistence.room.Dao;
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
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Delete
    void deleteAll(List<Category> categories);
}