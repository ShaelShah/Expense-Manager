package com.shael.shah.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<Expense> expenses;
    private List<Category> categories;

    private Toolbar toolbar;
    private TextView netTextView;
    private TextView incomeTexView;
    private TextView expensesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenses = getExpensesListFromSharedPreferences();
        categories = getCategoriesListFromSharedPreferences();

        netTextView = (TextView) findViewById(R.id.netTextView);
        incomeTexView = (TextView) findViewById(R.id.incomeTextView);
        expensesTextView = (TextView) findViewById(R.id.expensesTextView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_item:
                Intent intent = new Intent(this, AddExpenseActivity.class);
                startActivity(intent);

                return true;
            case R.id.open_menu:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (expenses != null) {
            Log.d("onPause - expenses", expenses.toString());
            setSharedPreferences(expenses, "expenses");
        }
        if (categories != null) {
            Log.d("onPause - categories", categories.toString());
            setSharedPreferences(categories, "categories");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        expenses = getExpensesListFromSharedPreferences();
        categories = getCategoriesListFromSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        expenses = getExpensesListFromSharedPreferences();
        categories = getCategoriesListFromSharedPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (expenses != null) {
            setSharedPreferences(expenses, "expenses");
        }
        if (categories != null) {
            setSharedPreferences(categories, "categories");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (expenses != null) {
            setSharedPreferences(expenses, "expenses");
        }
        if (categories != null) {
            setSharedPreferences(categories, "categories");
        }
    }

    public Boolean addCategory(String category) {
        for (Category c : categories) {
            if (c.getType().equals(category)) {
                return false;
            }
        }

        categories.add(new Category(category));
        return true;
    }

    public void setSharedPreferences(List<?> list, String tag) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);

        prefEditor.putString(tag, json);
        prefEditor.apply();
    }

    public List<Expense> getExpensesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("expenses", "");

        Type type = new TypeToken<List<Expense>>() {}.getType();
        List<Expense> expenses = gson.fromJson(json, type);

        if (expenses == null || expenses.isEmpty()) {
            return new ArrayList<>();
        }

        return expenses;
    }

    public List<Category> getCategoriesListFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("categories", "");

        Type type = new TypeToken<List<Category>>() {}.getType();
        List<Category> categories = gson.fromJson(json, type);

        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        return categories;
    }
}
