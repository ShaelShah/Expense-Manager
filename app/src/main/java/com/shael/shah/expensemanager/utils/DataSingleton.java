package com.shael.shah.expensemanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataSingleton {

    private static final String SHAREDPREF_EXPENSES = "com.shael.shah.expensemanager.SHAREDPREF_EXPENSES";
    private static final String SHAREDPREF_CATEGORIES = "com.shael.shah.expensemanager.SHAREDPREF_CATEGORIES";
    private static final String SHAREDPREF_COLORS = "com.shael.shah.expensemanager.SHAREDPREF_COLORS";

    @SuppressLint("StaticFieldLeak")
    private static DataSingleton instance;
    private static int currentColor = 0;
    private Context context;
    private List<Expense> expenses;
    private List<Category> categories;
    private int[] colors;

    private DataSingleton(Context context) {
        this.context = context;

        expenses = getExpensesListFromSharedPreferences();
        categories = getCategoriesListFromSharedPreferences();
        colors = context.getResources().getIntArray(R.array.categoryColors);
        currentColor = getColorFromSharedPreferences();
    }

//    public static DataSingleton getInstance(Context context) {
//        if (instance == null) {
//            instance = new DataSingleton(context.getApplicationContext());
//        }
//        return instance;
//    }

//    public static DataSingleton getInstance() {
//        if (instance != null)
//            return instance;
//
//        throw new IllegalArgumentException("DataSingleton has not been initialized");
//    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addExpense(Expense expense) {
        boolean added = false;

        if (!expenses.isEmpty()) {
            for (int i = 0; i < expenses.size(); i++) {
                if (expense.getDate().compareTo(expenses.get(i).getDate()) >= 0) {
                    expenses.add(i, expense);
                    added = true;
                    break;
                }
            }
        }

        if (!added)
            expenses.add(expense);
    }

    public boolean removeExpense(Expense expense) {
        for (Expense e : expenses) {
            if (e.equals(expense)) {
                expenses.remove(expense);
                return true;
            }
        }

        return false;
    }

    public Boolean addCategory(String category) {
        for (Category c : categories) {
            if (c.getType().equals(category)) {
                return false;
            }
        }

        categories.add(new Category(category, colors[currentColor++]));
        return true;
    }

    public void saveLists() {
        setSharedPreferences(expenses, SHAREDPREF_EXPENSES);
        setSharedPreferences(categories, SHAREDPREF_CATEGORIES);
        setSharedPreferenceColor(currentColor);
    }

    private List<Expense> getExpensesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHAREDPREF_EXPENSES, "");

        Type type = new TypeToken<List<Expense>>() {
        }.getType();
        List<Expense> expenses = gson.fromJson(json, type);

        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }

        return expenses;
    }

    private List<Category> getCategoriesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHAREDPREF_CATEGORIES, "");

        Type type = new TypeToken<List<Category>>() {
        }.getType();
        List<Category> categories = gson.fromJson(json, type);

        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        return categories;
    }

    private int getColorFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(SHAREDPREF_COLORS, 0);
    }

    private void setSharedPreferences(List<?> list, String tag) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);

        prefEditor.putString(tag, json);
        prefEditor.apply();
    }

    private void setSharedPreferenceColor(int stopColor) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putInt(SHAREDPREF_COLORS, stopColor);
        prefEditor.apply();
    }

    private void removeAllCategories() {
        categories.clear();
    }

    private void removeAllExpenses() {
        expenses.clear();
    }

    private void resetColor() {
        currentColor = 0;
    }

    public void reset() {
        removeAllExpenses();
        removeAllCategories();
        resetColor();
    }
}
