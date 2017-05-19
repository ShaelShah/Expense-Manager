package com.shael.shah.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    /*****************************************************************
    * Private Variables
    ******************************************************************/

    private List<Expense> expenses;
    private List<Category> categories;

    private Toolbar toolbar;
    private TextView netTextView;
    private TextView incomeTexView;
    private TextView expensesTextView;
    private ScrollView mainCategoryScrollView;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        //Find views to work with during main activity
        netTextView = (TextView) findViewById(R.id.netTextView);
        incomeTexView = (TextView) findViewById(R.id.incomeTextView);
        expensesTextView = (TextView) findViewById(R.id.expensesTextView);
        mainCategoryScrollView = (ScrollView) findViewById(R.id.mainCategoryScrollView);

        //Helper functions
        getLists();
        createRecurringExpenses();
        populateMoneyTextViews();
        createMainCategoryRows();

        //Workaround to delete all expenses/categories programmatically
        //Singleton.getInstance(this).removeAllExpenses();
        //Singleton.getInstance(this).removeAllCategories();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLists();
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

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

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

    /*****************************************************************
     * Helper Methods
     *****************************************************************/

    //Iterates through all expenses to calculate income and money spent
    private void populateMoneyTextViews() {
        BigDecimal income = new BigDecimal(0);
        BigDecimal outcome = new BigDecimal(0);
        BigDecimal netcome;

        for (Expense e : expenses) {
            if (e.isIncome()) {
                income = income.add(e.getAmount());
            } else {
                outcome = outcome.add(e.getAmount());
            }
        }

        //TODO: There should be a better way to do this
        netcome = (income.subtract(outcome)).abs();

        //TODO: Concatenations should not be used with setText
        incomeTexView.setText("$" + income);
        expensesTextView.setText("$" + outcome);
        netTextView.setText("$" + netcome);

        incomeTexView.setTextColor(Color.GREEN);
        expensesTextView.setTextColor(Color.RED);
        if (netcome.signum() > 0) {
            netTextView.setTextColor(Color.GREEN);
        } else {
            netTextView.setTextColor(Color.RED);
        }
    }

    //Iterates through all expenses to create new expenses if recurring = true
    private void createRecurringExpenses() {
        //TODO: This function needs to be tested. There is probably a better way to implement this function. Check out the Joda-Time library.
        Calendar calendar = Calendar.getInstance();

        for (Expense e : expenses) {
            if (e.isRecurring()) {
                calendar.setTime(e.getDate());

                switch (e.getRecurringPeriod()) {
                    case "Daily":
                        calendar.add(Calendar.DATE, 1);
                        break;
                    case "Weekly":
                        calendar.add(Calendar.DATE, 7);
                        break;
                    case "Monthly":
                        calendar.add(Calendar.MONTH, 1);
                        break;
                    case "Yearly":
                        calendar.add(Calendar.YEAR, 1);
                        break;
                }

                if (calendar.getTime().compareTo(e.getDate()) < 0) {
                    Expense newExpense = new Expense(calendar.getTime(), e.getAmount(), e.getCategory(), e.getLocation(), e.getNote(), true, e.isIncome(), e.getRecurringPeriod());
                    Singleton.getInstance(this).addExpense(newExpense);
                    e.setRecurring(false);
                }
            }
        }
    }

    //Iterates through categories and inflates a layout for each category with expenses totalling more than 0
    private void createMainCategoryRows() {
        LinearLayout scrollLinearLayout = (LinearLayout) mainCategoryScrollView.findViewById(R.id.mainScrollLinearLayout);

        for (Category c : categories) {
            //TODO: Look into View.inflate method (specifically the 3rd parameter)
            View item = View.inflate(this, R.layout.main_category_row_layout, null);

            //TODO: Color should be set dynamically and uniquely for each category
            View colorBox = item.findViewById(R.id.mainColorView);
            colorBox.setBackgroundColor(Color.RED);

            String title = c.getType();
            TextView categoryRowTitle = (TextView) item.findViewById(R.id.categoryRowTitle);
            categoryRowTitle.setText(title);

            BigDecimal amount = new BigDecimal(0);
            TextView categoryRowAmount = (TextView) item.findViewById(R.id.categryRowAmount);
            for (Expense e : expenses) {
                if (!e.isIncome() && e.getCategory().getType().equals(title)) {
                    amount = amount.add(e.getAmount());
                }
            }

            if (amount.signum() > 0) {
                //TODO: Concatenations should not be used with setText
                categoryRowAmount.setText("$" + amount);
                scrollLinearLayout.addView(item);
            }
        }
    }

    //Retrieves all expenses and categories
    private void getLists() {
        expenses = Singleton.getInstance(this).getExpenses();
        categories = Singleton.getInstance(this).getCategories();
    }

    //Sets all expenses and categories
    private void setLists() {
        Singleton.getInstance(this).saveLists();
    }
}
