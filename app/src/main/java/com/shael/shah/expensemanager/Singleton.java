package com.shael.shah.expensemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//TODO: Figure out this warning
public class Singleton {

    //TODO: Figure out this warning
    private static Singleton instance;
    private Context context;

    private List<Expense> expenses;
    private List<Category> categories;

    //TODO: Colors should be defined? dynamically and should theoretically be limitless
    private static int currentColor = 0;
    private int[] colors;


    //TODO: Figure out this warning
    public static Singleton getInstance(Context context) {
        if (instance == null) {
            instance = new Singleton(context);
        }
        return instance;
    }

    private Singleton(Context context) {
        this.context = context;

        expenses = getExpensesListFromSharedPreferences();
        categories = getCategoriesListFromSharedPreferences();
        colors = context.getResources().getIntArray(R.array.categoryColors);
        currentColor = getColorFromSharedPreferences();
    }

    //TODO: Figure out this warning
    public List<Expense> getExpenses() {
        return expenses;
    }

    //TODO: Figure out this warning
    public List<Category> getCategories() {
        return categories;
    }

    //TODO: Figure out this warning
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        for (Expense e : expenses) {
            if (e.equals(expense)) {
                expenses.remove(expense);
                break;
            }
        }
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

    //TODO: Figure out this warning
    public void saveLists() {
        setSharedPreferences(expenses, "expenses");
        setSharedPreferences(categories, "categories");
        setSharedPreferenceColor(currentColor);
    }

    private List<Expense> getExpensesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("expenses", "");

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
        String json = sharedPreferences.getString("categories", "");

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
        return sharedPreferences.getInt("Color", 0);
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

        prefEditor.putInt("color", stopColor);
        prefEditor.apply();
    }

    public void reset() {
        removeAllCategories();
        removeAllExpenses();
        currentColor = 0;
    }

    private void removeAllCategories() {
        categories.clear();
    }

    private void removeAllExpenses() {
        expenses.clear();
    }
}
