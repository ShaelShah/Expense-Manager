package com.shael.shah.expensemanager;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ArrayList<Expense> expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenses = getListFromSharedPreferences();
    }

    public ArrayList<Expense> getListFromSharedPreferences() {
        return new ArrayList<>();
    }
}
