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

        getLists();

        Log.d("onCreate - expenses", expenses.toString());
        Log.d("onCreate - categories", categories.toString());

        //expenses = getExpensesListFromSharedPreferences();
        //categories = getCategoriesListFromSharedPreferences();

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
                //intent.putExtra("expenses", expenses);
                //intent.putExtra("categories", categories);
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
        setLists();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLists();

        Log.d("onStart - expenses", expenses.toString());
        Log.d("onStart - categories", categories.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLists();

        Log.d("onResume - expenses", expenses.toString());
        Log.d("onResume - categories", categories.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        setLists();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setLists();
    }

    private void getLists() {
        expenses = Singleton.getInstance(this).getExpenses();
        categories = Singleton.getInstance(this).getCategories();
    }

    private void setLists() {
        Singleton.getInstance(this).saveLists();
        /*if (expenses != null) {
            setSharedPreferences(expenses, "expenses");
        }
        if (categories != null) {
            setSharedPreferences(categories, "categories");
        }*/
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
